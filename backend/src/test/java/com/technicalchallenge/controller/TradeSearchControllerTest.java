package com.technicalchallenge.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.mapper.TradeMapper;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.service.TradeSearchService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TradeSearchController.class)
public class TradeSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TradeSearchService tradeSearchService;

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

        when(tradeSearchService.getAllTrades(any(), any(), any())).thenReturn(trades);
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
        verify(tradeSearchService).getAllTrades(any(), any(),
                any());
    }

    /**
     * Tests expected response code when the filtered search returns empty list
     */
    @Test
    void testGetTradesByFilterNoContent() throws Exception {
        // Given - Mocked page and service returns empty list
        Page<Trade> trades = new PageImpl<>(Collections.emptyList());
        when(tradeSearchService.getAllTrades(any(), any(), any())).thenReturn(trades);

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
        verify(tradeSearchService).getAllTrades(any(), any(),
                any());
    }

    /**
     * Tests expected response code when a trade has been searched using RSQL
     */
    @Test
    void testGetAllTradesByRSQL() throws Exception {
        // Given - New Trade and mocked service method
        List<Trade> trades = List.of(trade);
        when(tradeSearchService.getAllTradesByRSQL(any())).thenReturn(trades);

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
        verify(tradeSearchService).getAllTradesByRSQL(any());
    }

    /**
     * Tests expected response code when the rsql search returns empty list
     */
    @Test
    void testGetAllTradesByRsqlNoContent() throws Exception {
        // Given - Mocked Service returns empty list
        when(tradeSearchService.getAllTradesByRSQL(any())).thenReturn(List.of());

        // When/Then
        // set up a GET request to a test endpoint
        mockMvc.perform(get("/api/trades/rsql")
                // parameters used - query(tradeId that does not exist)
                .param("query",
                        "tradeId==1002L"))
                // expect response status 204 NO CONTENT
                .andExpect(status().isNoContent());
        // Verifies the search happened once
        verify(tradeSearchService).getAllTradesByRSQL(any());
    }

    /**
     * Tests expected response code when a trade has been searched
     */
    @Test
    void testGetAllTradesByCriteria() throws Exception {
        // Given - New Trade and mocked service method
        List<Trade> trades = List.of(trade);
        when(tradeSearchService.getAllTradesByCriteria(any())).thenReturn(trades);

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
        verify(tradeSearchService).getAllTradesByCriteria(any());
    }

    /**
     * Tests expected response code when the search returns empty list
     */
    @Test
    void testGetAllTradesByCriteriaNoContent() throws Exception {
        // Given - Mocked Service returns empty list
        when(tradeSearchService.getAllTradesByCriteria(any())).thenReturn(List.of());

        // When/Then
        // set up a GET request to a test endpoint
        mockMvc.perform(get("/api/trades/search")
                // parameters used - bookName that doesn't exist
                .param("bookName", "NonExistentBook"))
                // expect response status 204 NO CONTENT
                .andExpect(status().isNoContent());
        // Verifies the search happened once
        verify(tradeSearchService).getAllTradesByCriteria(any());
    }

}
