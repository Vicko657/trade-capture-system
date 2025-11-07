package com.technicalchallenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.mapper.TradeMapper;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TradeController.class)
public class TradeControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private TradeService tradeService;

        @MockBean
        private TradeMapper tradeMapper;

        private ObjectMapper objectMapper;
        private TradeDTO tradeDTO;
        private Trade trade;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                // Create a sample TradeDTO for testing
                tradeDTO = new TradeDTO();
                tradeDTO.setId(1L);
                tradeDTO.setTradeId(1001L);
                tradeDTO.setVersion(1);
                tradeDTO.setActive(true);
                tradeDTO.setTradeDate(LocalDate.now()); // Fixed: LocalDate instead of LocalDateTime
                tradeDTO.setTradeStartDate(LocalDate.now().plusDays(2)); // Fixed: correct method name
                tradeDTO.setTradeMaturityDate(LocalDate.now().plusYears(5)); // Fixed: correct method name
                tradeDTO.setTradeStatus("LIVE");
                tradeDTO.setBookName("TestBook");
                tradeDTO.setCounterpartyName("TestCounterparty");
                tradeDTO.setTraderUserName("TestTrader");
                tradeDTO.setInputterUserName("TestInputter");
                tradeDTO.setUtiCode("UTI123456789");

                // Create a sample Trade entity for testing
                trade = new Trade();
                trade.setId(1L);
                trade.setTradeId(1001L);
                trade.setVersion(1);
                trade.setTradeDate(LocalDate.now()); // Fixed: LocalDate instead of LocalDateTime
                trade.setTradeStartDate(LocalDate.now().plusDays(2)); // Fixed: correct method name
                trade.setTradeMaturityDate(LocalDate.now().plusYears(5)); // Fixed: correct method name

                // Set up default mappings
                when(tradeMapper.toDto(any(Trade.class))).thenReturn(tradeDTO);
                when(tradeMapper.toEntity(any(TradeDTO.class))).thenReturn(trade);
        }

        /**
         * Tests expected response code when a page of results has been returned with
         * the filter endpoint
         */
        @Test
        void testGetFilteredTradesPaginatedAndSortedByFilter() throws Exception {
                // Given - New 2nd Trade, new counterparty, mocked page and service method
                // Counterparty
                Counterparty counterparty = new Counterparty();
                counterparty.setId(1L);
                counterparty.setName("TestCounterpartyA");

                // Trade Entity and DTO
                trade.setCounterparty(counterparty);
                tradeDTO.setCounterpartyId(1L);
                tradeDTO.setCounterpartyName("TestCounterpartyA");

                // 2nd Trade Entity
                Trade trade2 = new Trade();
                trade2.setId(2L);
                trade2.setCounterparty(counterparty);

                // 2nd TradeDTO
                TradeDTO tradeDTO2 = new TradeDTO();
                tradeDTO2.setId(2L);
                tradeDTO2.setCounterpartyId(1L);
                tradeDTO2.setCounterpartyName("TestCounterpartyA");

                List<Trade> tradeDTOlList = List.of(trade2, trade);
                Page<Trade> trades = new PageImpl<>(tradeDTOlList);

                when(tradeService.getAllTrades(any(), any(), any())).thenReturn(trades);
                when(tradeMapper.toDto(trade)).thenReturn(tradeDTO);
                when(tradeMapper.toDto(trade2)).thenReturn(tradeDTO2);

                // When/Then
                // set up a GET request to a test endpoint
                mockMvc.perform(get("/api/trades/filter")
                                // parameters used - criteria(counterpartyName), pagination(pageNo,pageSize) and
                                // sort(sortBy,sortDir)
                                .param("counterpartyName",
                                                "TestCounterpartyA")
                                .param("pageNo", "1")
                                .param("pageSize", "3")
                                .param("sortBy", "id")
                                .param("sortDir", "desc")
                                .accept(MediaType.APPLICATION_JSON))
                                // expect response status 200 OK REQUEST
                                .andExpect(status().isOk())
                                // expect JSON path to return two trades in descending order
                                .andExpect(jsonPath("$.content.length()").value(2))
                                .andExpect(jsonPath("$.content[0].id", is(2)))
                                .andExpect(jsonPath("$.content[1].id", is(1)));
                // Verifies the search happened once
                verify(tradeService).getAllTrades(any(), any(),
                                any());
        }

        /**
         * Tests expected response code when the filtered search returns empty list
         */
        @Test
        void testGetTradesByFilterNoContent() throws Exception {
                // Given - Mocked page and service returns empty list
                Page<Trade> trades = new PageImpl<>(Collections.emptyList());
                when(tradeService.getAllTrades(any(), any(), any())).thenReturn(trades);

                // When/Then
                // set up a GET request to a test endpoint
                mockMvc.perform(get("/api/trades/filter")
                                // parameters used - criteria(bookName that does not exist)
                                .param("bookName",
                                                "TestBookB")
                                .param("pageNo", "2")
                                .param("pageSize", "6")
                                .param("sortBy", "id")
                                .param("sortDir", "asc"))
                                // expect response status 204 NO CONTENT
                                .andExpect(status().isNoContent());
                // Verifies the search happened once
                verify(tradeService).getAllTrades(any(), any(),
                                any());
        }

        /**
         * Tests expected response code when a trade has been searched using RSQL
         */
        @Test
        void testGetAllTradesByRSQL() throws Exception {
                // Given - New Trade and mocked service method
                List<Trade> trades = List.of(trade);
                when(tradeService.getAllTradesByRSQL(any())).thenReturn(trades);

                // When/Then
                // set up a GET request to a test endpoint
                mockMvc.perform(get("/api/trades/rsql")
                                // parameters used - query(bookName and utiCode)
                                .param("query",
                                                "book.bookName==TestBook;utiCode==UTI123456789")
                                // expect JSON to be returned
                                .contentType(MediaType.APPLICATION_JSON))
                                // expect response status 200 OK REQUEST
                                .andExpect(status().isOk())
                                // expect JSON path to return one trade and have matching bookName and
                                // counterpartyName
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].bookName", is("TestBook")))
                                .andExpect(jsonPath("$[0].counterpartyName", is("TestCounterparty")));
                // Verifies the search happened once
                verify(tradeService).getAllTradesByRSQL(any());
        }

        /**
         * Tests expected response code when the rsql search returns empty list
         */
        @Test
        void testGetAllTradesByRsqlNoContent() throws Exception {
                // Given - Mocked Service returns empty list
                when(tradeService.getAllTradesByRSQL(any())).thenReturn(List.of());

                // When/Then
                // set up a GET request to a test endpoint
                mockMvc.perform(get("/api/trades/rsql")
                                // parameters used - query(tradeId that does not exist)
                                .param("query",
                                                "tradeId==1002L"))
                                // expect response status 204 NO CONTENT
                                .andExpect(status().isNoContent());
                // Verifies the search happened once
                verify(tradeService).getAllTradesByRSQL(any());
        }

        /**
         * Tests expected response code when a trade has been searched
         */
        @Test
        void testGetAllTradesByCriteria() throws Exception {
                // Given - New Trade and mocked service method
                List<Trade> trades = List.of(trade);
                when(tradeService.getAllTradesByCriteria(any())).thenReturn(trades);

                // When/Then
                // set up a GET request to a test endpoint
                mockMvc.perform(get("/api/trades/search")
                                // parameters used - bookName and tradeStatus
                                .param("bookName", "TestBook")
                                .param("tradeStatus", "LIVE")
                                // expect JSON to be returned
                                .contentType(MediaType.APPLICATION_JSON))
                                // expect response status 200 OK REQUEST
                                .andExpect(status().isOk())
                                // expect JSON path to return one trade and have matching bookName and
                                // tradeStatus
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].bookName", is("TestBook")))
                                .andExpect(jsonPath("$[0].tradeStatus", is("LIVE")));
                // Verifies the search happened once
                verify(tradeService).getAllTradesByCriteria(any());
        }

        /**
         * Tests expected response code when the search returns empty list
         */
        @Test
        void testGetAllTradesByCriteriaNoContent() throws Exception {
                // Given - Mocked Service returns empty list
                when(tradeService.getAllTradesByCriteria(any())).thenReturn(List.of());

                // When/Then
                // set up a GET request to a test endpoint
                mockMvc.perform(get("/api/trades/search")
                                // parameters used - bookName that doesn't exist
                                .param("bookName", "NonExistentBook"))
                                // expect response status 204 NO CONTENT
                                .andExpect(status().isNoContent());
                // Verifies the search happened once
                verify(tradeService).getAllTradesByCriteria(any());
        }

        @Test
        void testGetAllTrades() throws Exception {
                // Given
                List<Trade> trades = List.of(trade); // Fixed: use List.of instead of Arrays.asList for single item

                when(tradeService.getAllTrades()).thenReturn(trades);

                // When/Then
                mockMvc.perform(get("/api/trades")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].tradeId", is(1001)))
                                .andExpect(jsonPath("$[0].bookName", is("TestBook")))
                                .andExpect(jsonPath("$[0].counterpartyName", is("TestCounterparty")));

                verify(tradeService).getAllTrades();
        }

        @Test
        void testGetTradeById() throws Exception {
                // Given
                when(tradeService.getTradeById(1001L)).thenReturn(Optional.of(trade));

                // When/Then
                mockMvc.perform(get("/api/trades/1001")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.tradeId", is(1001)))
                                .andExpect(jsonPath("$.bookName", is("TestBook")))
                                .andExpect(jsonPath("$.counterpartyName", is("TestCounterparty")));

                verify(tradeService).getTradeById(1001L);
        }

        @Test
        void testGetTradeByIdNotFound() throws Exception {
                // Given
                when(tradeService.getTradeById(9999L)).thenReturn(Optional.empty());

                // When/Then
                mockMvc.perform(get("/api/trades/9999")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(tradeService).getTradeById(9999L);
        }

        @Test
        void testCreateTrade() throws Exception {
                // Given
                when(tradeService.saveTrade(any(Trade.class), any(TradeDTO.class))).thenReturn(trade);
                doNothing().when(tradeService).populateReferenceDataByName(any(Trade.class), any(TradeDTO.class));

                // When/Then
                mockMvc.perform(post("/api/trades")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tradeDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.tradeId", is(1001)));

                verify(tradeService).saveTrade(any(Trade.class), any(TradeDTO.class));
                verify(tradeService).populateReferenceDataByName(any(Trade.class), any(TradeDTO.class));
        }

        @Test
        void testCreateTradeValidationFailure_MissingTradeDate() throws Exception {
                // Given
                TradeDTO invalidDTO = new TradeDTO();
                invalidDTO.setBookName("TestBook");
                invalidDTO.setCounterpartyName("TestCounterparty");
                // Trade date is purposely missing

                // When/Then
                mockMvc.perform(post("/api/trades")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDTO)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value("Trade date is required"));

                verify(tradeService, never()).saveTrade(any(Trade.class), any(TradeDTO.class));
        }

        @Test
        void testCreateTradeValidationFailure_MissingBook() throws Exception {
                // Given
                TradeDTO invalidDTO = new TradeDTO();
                invalidDTO.setTradeDate(LocalDate.now());
                invalidDTO.setCounterpartyName("TestCounterparty");
                // Book name is purposely missing

                // When/Then
                mockMvc.perform(post("/api/trades")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDTO)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value("Book name is required"));

                verify(tradeService, never()).saveTrade(any(Trade.class), any(TradeDTO.class));
        }

        @Test
        void testCreateTradeValidationFailure_MissingCounterparty() throws Exception {
                // Given
                TradeDTO invalidDTO = new TradeDTO();
                invalidDTO.setTradeDate(LocalDate.now());
                invalidDTO.setBookName("TestBook");
                // Counterparty is purposely missing

                // When/Then
                mockMvc.perform(post("/api/trades")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDTO)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value("Counterparty name is required"));

                verify(tradeService, never()).saveTrade(any(Trade.class), any(TradeDTO.class));
        }

        /**
         * Tests that a trade can be amended
         */
        @Test
        void testUpdateTrade() throws Exception {
                // Given
                Long tradeId = 1001L;
                tradeDTO.setTradeId(tradeId);
                when(tradeService.amendTrade(eq(tradeId), any(TradeDTO.class))).thenReturn(trade); // Mocked amendTrade
                                                                                                   // stubbing
                                                                                                   // statement
                when(tradeMapper.toDto(any(Trade.class))).thenReturn(tradeDTO); // Mocked toDTO trade mapper

                // When/Then
                // set up a PUT request to a test endpoint
                mockMvc.perform(put("/api/trades/{id}", tradeId)
                                // expect JSON to be returned
                                .contentType(MediaType.APPLICATION_JSON)
                                // expect Mapper used to return tradeDTO
                                .content(objectMapper.writeValueAsString(tradeDTO)))
                                // expect response status 200 OK REQUEST
                                .andExpect(status().isOk())
                                // expect json path tradeId = 1001
                                .andExpect(jsonPath("$.tradeId", is(1001)));
                // Checks if amendTrade mock had happened
                verify(tradeService).amendTrade(eq(tradeId), any(TradeDTO.class));
        }

        /**
         * Tests expected response code when a tradeId isn't matching the path Id
         */
        @Test
        void testUpdateTradeIdMismatch() throws Exception {
                // Given
                Long pathId = 1001L;
                tradeDTO.setTradeId(2002L); // Different from path ID

                // When/Then
                // set up a PUT request to a test endpoint - endpoint changed to
                mockMvc.perform(put("/api/trades/{id}", pathId)
                                // expect JSON to be returned
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tradeDTO)))
                                // expect response status 400 BAD REQUEST
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string(
                                                "Trade ID in path must match Trade ID in request body"));
                // Verifies that amendTrade was never called in the test
                verify(tradeService, never()).amendTrade(pathId, tradeDTO);
        }

        /**
         * Tests expected response code when a trade has been deleted
         */
        @Test
        void testDeleteTrade() throws Exception {
                // Given
                doNothing().when(tradeService).deleteTrade(1001L);

                // When/Then
                // set up a DELETE request to a test endpoint
                mockMvc.perform(delete("/api/trades/1001")
                                // expect JSON to be returned
                                .contentType(MediaType.APPLICATION_JSON))
                                // expect response status 204 NO CONTENT REQUEST
                                .andExpect(status().isNoContent());
                // Checks if trade has been deleted
                verify(tradeService).deleteTrade(1001L);
        }

        @Test
        void testCreateTradeWithValidationErrors() throws Exception {
                // Given
                TradeDTO invalidDTO = new TradeDTO();
                invalidDTO.setTradeDate(LocalDate.now()); // Fixed: LocalDate instead of LocalDateTime
                // Missing required fields to trigger validation errors

                // When/Then
                mockMvc.perform(post("/api/trades")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDTO)))
                                .andExpect(status().isBadRequest());

                verify(tradeService, never()).createTrade(any(TradeDTO.class));
        }
}
