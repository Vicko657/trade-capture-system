package com.technicalchallenge.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.repository.TradeRepository;

import jakarta.transaction.Transactional;

/**
 * Integration Tests for Trade Controller
 */

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TradeControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private TradeRepository tradeRepository;

        @Autowired
        private ObjectMapper mapper;

        @BeforeEach
        void setUp() {

                tradeRepository.deleteAll();
        }

        /**
         * Multi Criteria Search: Tests expected response code when a trade has been
         * searched
         */
        @Test
        void shouldReturnTradesMatchingSearchCriteria() throws Exception {

                mockMvc.perform(get("/api/trades/search")
                                .param("traderUserFirstName", "Stacey")
                                .param("tradeStatus", "NEW")
                                .contentType(
                                                MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].bookName").value("FX-BOOK-1"))
                                .andExpect(jsonPath("$[1].counterpartyName").value("BigBank"))
                                .andExpect(jsonPath("$[0].tradeStatus").value("NEW"))
                                .andExpect(jsonPath("$.length()").value(6));
        }

        /**
         * Multi Criteria Search: Tests expected response code when no matches have been
         * found
         */
        @Test
        void shouldReturnNoContentWhenNoMatchingTrades() throws Exception {

                mockMvc.perform(get("/api/trades/search")
                                .param("bookName", "N/A")
                                .contentType(
                                                MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());
        }

        /**
         * RSQL Search: Tests expected response code when a trade has been
         * searched by the same Counterparty
         */
        @Test
        void shouldReturnTradesMatchingCounterparty() throws Exception {

                mockMvc.perform(get("/api/trades/rsql")
                                .param("query", "counterparty.name==MegaFund")
                                .contentType(
                                                MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].counterpartyName").value("MegaFund"))
                                .andExpect(jsonPath("$[0].tradeStatus").value("NEW"))
                                .andExpect(jsonPath("$.length()").value(5));
        }

        /**
         * RSQL Search: Tests expected response code when a trade has been searched
         * using the complex multi criteria
         * 
         */
        @Test
        void shouldReturnTradesMatchingComplexMultiCriteriaSearch() throws Exception {

                mockMvc.perform(get("/api/trades/rsql")
                                .param("query", "(counterparty.name==BigBank,counterparty.name==MegaFund);tradeStatus.tradeStatus==NEW")
                                .contentType(
                                                MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].bookName").value("FX-BOOK-1"))
                                .andExpect(jsonPath("$[1].counterpartyName").value("BigBank"))
                                .andExpect(jsonPath("$[0].tradeStatus").value("NEW"))
                                .andExpect(jsonPath("$.length()").value(6));
        }

        /**
         * RSQL Search: Tests expected response code when a trade has been
         * searched using date range
         */
        @Test
        void shouldReturnTradesMatchingDateRange() throws Exception {

                mockMvc.perform(get("/api/trades/rsql")
                                .param("query", "tradeDate=ge=2025-01-01;tradeDate=le=2025-12-31")
                                .contentType(
                                                MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].tradeDate").value("2025-02-04"))
                                .andExpect(jsonPath("$[1].tradeDate").value("2025-03-30"))
                                .andExpect(jsonPath("$.length()").value(6));
        }

        /**
         * Paginated Filter Search: Tests expected response code when results have been
         * paginated
         */
        @Test
        void shouldReturnPaginatedResults() throws Exception {

                mockMvc.perform(get("/api/trades/filter")
                                .param("pageNo", "1")
                                .param("pageSize", "20")
                                .contentType(
                                                MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(6))
                                .andExpect(jsonPath("$.totalElements").value(6))
                                .andExpect(jsonPath("$.content[0].bookName").value("FX-BOOK-1"))
                                .andExpect(jsonPath("$.content[0].tradeStatus").value("NEW"));
        }

        /**
         * Paginated Filter Search: Tests expected response code when results have been
         * paginated and sorted
         */
        @Test
        void shouldReturnSortedResults() throws Exception {

                mockMvc.perform(get("/api/trades/filter")
                                .param("pageNo", "1")
                                .param("pageSize", "10")
                                .param("sortBy", "tradeId")
                                .param("sortDir", "DESC")
                                .contentType(
                                                MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(6))
                                .andExpect(jsonPath("$.totalElements").value(6))
                                .andExpect(jsonPath("$.content[0].tradeId").value(100008L))
                                .andExpect(jsonPath("$.content[5].tradeId").value(100003L));
        }

        /**
         * Paginated Filter Search: Tests expected response code when results have been
         * filtered, sorted and paginated
         */
        @Test
        void shouldReturnCombinedFilteredPaginatedAndSortedResults() throws Exception {

                mockMvc.perform(get("/api/trades/filter")
                                .param("bookName",
                                                "FX-BOOK-1")
                                .param("sortBy", "utiCode")
                                .param("sortDir", "asc")
                                .param("pageNo", "1")
                                .param("pageSize", "4")
                                .contentType(
                                                MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(3))
                                .andExpect(jsonPath("$.totalElements").value(3))
                                .andExpect(jsonPath("$.content[2].bookName").value("FX-BOOK-1"))
                                .andExpect(jsonPath("$.content[0].utiCode").value("UTI-003"));
        }

        /**
         * Paginated Filter Search: Tests expected response code when no results have
         * been found
         */
        @Test
        void shouldReturnNoContentWhenNoResults() throws Exception {

                mockMvc.perform(get("/api/trades/filter")
                                .param("counterpartyName", "N/A")
                                .param("sortBy", "id")
                                .param("sortDir", "desc")
                                .param("pageNo", "1")
                                .param("pageSize", "4")
                                .contentType(
                                                MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());
        }

        /**
         * Trade Creation with Cashflow Generation: Tests expected response code Created
         */
        @Test
        void shouldReturnGeneratedCashflows() throws Exception {

                // Created Trade
                TradeDTO tradeDTO = new TradeDTO();
                tradeDTO.setBookId(1000L);
                tradeDTO.setBookName("RATES-BOOK-1");
                tradeDTO.setCounterpartyId(1000L);
                tradeDTO.setCounterpartyName("BigBank");
                tradeDTO.setTradeInputterUserId(1003L);
                tradeDTO.setTraderUserName("Simon King");
                tradeDTO.setTraderUserId(1003L);
                tradeDTO.setInputterUserName("Simon King");
                tradeDTO.setTradeDate(LocalDate.of(2025, 12, 3));
                tradeDTO.setTradeStartDate(LocalDate.of(2025, 12, 10));
                tradeDTO.setTradeMaturityDate(LocalDate.of(2026, 12, 3));
                tradeDTO.setTradeExecutionDate(LocalDate.of(2025, 12, 3));
                tradeDTO.setTradeTypeId(1001L);
                tradeDTO.setTradeType("Swap");
                tradeDTO.setTradeSubTypeId(1003L);
                tradeDTO.setTradeSubType("IR Swap");
                tradeDTO.setTradeStatusId(1000L);
                tradeDTO.setTradeStatus("NEW");

                // Fixed Leg
                TradeLegDTO legDTO1 = new TradeLegDTO();
                legDTO1.setLegType("Fixed");
                legDTO1.setNotional(new BigDecimal(10000000));
                legDTO1.setRate(3.5);
                legDTO1.setIndexName(null);
                legDTO1.setPayRecId(1000L);
                legDTO1.setPayReceiveFlag("Pay");
                legDTO1.setCurrencyId(1000L);
                legDTO1.setCurrency("USD");
                legDTO1.setHolidayCalendarId(1000L);
                legDTO1.setHolidayCalendar("NY");
                legDTO1.setCalculationPeriodSchedule("Quarterly");
                legDTO1.setFixingBusinessDayConvention("Following");
                legDTO1.setPaymentBusinessDayConvention("Following");

                // Floating Leg
                TradeLegDTO legDTO2 = new TradeLegDTO();
                legDTO2.setLegType("Floating");
                legDTO2.setNotional(new BigDecimal(10000000));
                legDTO2.setIndexName("LIBOR");
                legDTO2.setPayRecId(1001L);
                legDTO2.setPayReceiveFlag("Receive");
                legDTO2.setCurrencyId(1000L);
                legDTO2.setCurrency("USD");
                legDTO2.setHolidayCalendarId(1000L);
                legDTO2.setHolidayCalendar("NY");
                legDTO2.setCalculationPeriodSchedule("Quarterly");
                legDTO2.setFixingBusinessDayConvention("Following");
                legDTO2.setPaymentBusinessDayConvention("Following");

                tradeDTO.setTradeLegs(List.of(legDTO1, legDTO2));

                mockMvc.perform(post("/api/trades")
                                .with(httpBasic("simon", "password"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(tradeDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.tradeId").value(10000))
                                .andExpect(jsonPath("$.tradeLegs.length()").value(2))

                                // Verifies fixed leg returns 3 cashflows with 87500 payment value
                                .andExpect(jsonPath("$.tradeLegs[0].cashflows.length()").value(3))
                                .andExpect(jsonPath("$.tradeLegs[0].cashflows[0].paymentValue").value(87500))
                                .andExpect(jsonPath("$.tradeLegs[0].cashflows[0].valueDate")
                                                .value("2026-03-10"))

                                // Verifies floating leg returns 3 cashflows with 0 payment value
                                .andExpect(jsonPath("$.tradeLegs[1].cashflows.length()").value(3))
                                .andExpect(jsonPath("$.tradeLegs[1].cashflows[0].paymentValue").value(0))
                                .andExpect(jsonPath("$.tradeLegs[1].cashflows[2].valueDate")
                                                .value("2026-09-10"));

        }

}
