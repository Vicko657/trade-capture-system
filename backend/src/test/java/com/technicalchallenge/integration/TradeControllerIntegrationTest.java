package com.technicalchallenge.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.model.Cashflow;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.model.Schedule;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.model.TradeLeg;
import com.technicalchallenge.model.TradeStatus;
import com.technicalchallenge.repository.ApplicationUserRepository;
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
        private ApplicationUserRepository applicationUserRepository;

        @BeforeEach
        void setUp() {

                tradeRepository.deleteAll();

                // Created related entites for Trade (Book, Counterparty, TradeStatus,
                // ApplicationUser(traderUser & InputterUser), Tradelegs, Schduele and Cashflow)
                // for persistence

                Book book1 = new Book();
                book1.setId(1000L);
                book1.setVersion(2);
                book1.setBookName("RATES-BOOK-2");
                book1.setActive(true);

                Book book2 = new Book();
                book2.setId(1001L);
                book2.setVersion(3);
                book2.setBookName("FX-BOOK-1");
                book2.setActive(true);

                Counterparty counterparty1 = new Counterparty();
                counterparty1.setId(1000L);
                counterparty1.setName("BigBank");
                counterparty1.setPhoneNumber("123-456-7890");
                counterparty1.setInternalCode(1001L);
                counterparty1.setLastModifiedDate(LocalDate.of(2025, 6, 2));
                counterparty1.setActive(true);
                counterparty1.setCreatedDate(LocalDate.of(2024, 1, 1));
                counterparty1.setAddress("1 Bank St");

                Counterparty counterparty2 = new Counterparty();
                counterparty2.setId(1001L);
                counterparty2.setName("MegaFund");
                counterparty2.setPhoneNumber("302-951-7610");
                counterparty2.setInternalCode(1002L);
                counterparty2.setLastModifiedDate(LocalDate.of(2025, 8, 12));
                counterparty2.setActive(true);
                counterparty2.setCreatedDate(LocalDate.of(2024, 1, 1));
                counterparty2.setAddress("1 Bank St");

                TradeStatus tradeStatus = new TradeStatus();
                tradeStatus.setId(1000L);
                tradeStatus.setTradeStatus("NEW");

                ApplicationUser traderUser = new ApplicationUser();
                traderUser.setId(1006L);
                traderUser.setFirstName("Stacey");
                traderUser.setLastName("Smith");
                traderUser.setLoginId("stacey");
                traderUser.setPassword("un7-xys-892");
                traderUser.setLastModifiedTimestamp(LocalDateTime.now());
                traderUser.setVersion(1);
                traderUser.setActive(true);

                ApplicationUser inputterUser = new ApplicationUser();

                inputterUser.setFirstName("Johnathan");
                inputterUser.setLastName("Wicker");
                inputterUser.setLoginId("johnathan");
                inputterUser.setPassword("un7-xys-892");
                inputterUser.setLastModifiedTimestamp(LocalDateTime.now());
                inputterUser.setVersion(1);
                inputterUser.setActive(true);

                Schedule schedule = new Schedule();
                schedule.setId(1001L);
                schedule.setSchedule("1M");

                List<Cashflow> cashflowList1 = new ArrayList<>();
                List<Cashflow> cashflowList2 = new ArrayList<>();

                TradeLeg tradeLeg1 = new TradeLeg();
                tradeLeg1.setActive(true);
                tradeLeg1.setNotional(BigDecimal.valueOf(1000000));
                tradeLeg1.setRate(0.05);
                tradeLeg1.setCalculationPeriodSchedule(schedule);
                tradeLeg1.setLegId(1003L);
                tradeLeg1.setCashflows(cashflowList1);

                TradeLeg tradeLeg2 = new TradeLeg();
                tradeLeg2.setActive(true);
                tradeLeg2.setNotional(BigDecimal.valueOf(1000000));
                tradeLeg2.setRate(0.05);
                tradeLeg2.setCalculationPeriodSchedule(schedule);
                tradeLeg2.setLegId(1004L);
                tradeLeg2.setCashflows(cashflowList2);

                List<TradeLeg> tradeLegs1 = new ArrayList<>();

                tradeLegs1.add(tradeLeg1);
                tradeLegs1.add(tradeLeg2);

                List<TradeLeg> tradeLegs2 = new ArrayList<>();

                tradeLegs2.add(tradeLeg1);
                tradeLegs2.add(tradeLeg2);

                Cashflow cashflow = new Cashflow();
                cashflow.setId(1000L);
                cashflow.setActive(true);
                cashflow.setRate(0.05);

                // Create test trades

                Trade trade1 = new Trade();
                trade1.setId(1003L);
                trade1.setTradeId(100003L);
                trade1.setVersion(1);
                trade1.setBook(book1);
                trade1.setCounterparty(counterparty2);
                trade1.setUtiCode("UTI-003");
                trade1.setTraderUser(traderUser);
                trade1.setTradeInputterUser(inputterUser);
                trade1.setTradeStatus(tradeStatus);
                trade1.setTradeDate(LocalDate.of(2025, 2, 4));
                trade1.setActive(true);
                trade1.setTradeLegs(tradeLegs1);

                Trade trade2 = new Trade();
                trade2.setId(1004L);
                trade2.setTradeId(100004L);
                trade2.setVersion(1);
                trade2.setBook(book2);
                trade2.setCounterparty(counterparty1);
                trade2.setUtiCode("UTI-004");
                trade2.setTraderUser(traderUser);
                trade2.setTradeInputterUser(inputterUser);
                trade2.setTradeStatus(tradeStatus);
                trade2.setTradeDate(LocalDate.of(2025, 3, 30));
                trade2.setActive(true);
                trade2.setTradeLegs(tradeLegs2);

                Trade trade3 = new Trade();
                trade3.setId(1005L);
                trade3.setTradeId(100005L);
                trade3.setVersion(1);
                trade3.setBook(book1);
                trade3.setCounterparty(counterparty2);
                trade3.setUtiCode("UTI-005");
                trade3.setTraderUser(traderUser);
                trade3.setTradeInputterUser(inputterUser);
                trade3.setTradeStatus(tradeStatus);
                trade3.setTradeDate(LocalDate.of(2025, 2, 4));
                trade3.setActive(true);
                trade3.setTradeLegs(tradeLegs1);

                Trade trade4 = new Trade();
                trade4.setId(1006L);
                trade4.setTradeId(100006L);
                trade4.setVersion(1);
                trade4.setBook(book2);
                trade4.setCounterparty(counterparty2);
                trade4.setUtiCode("UTI-006");
                trade4.setTraderUser(traderUser);
                trade4.setTradeInputterUser(inputterUser);
                trade4.setTradeStatus(tradeStatus);
                trade4.setTradeDate(LocalDate.of(2025, 3, 30));
                trade4.setActive(true);
                trade4.setTradeLegs(tradeLegs2);

                Trade trade5 = new Trade();
                trade5.setId(1005L);
                trade5.setTradeId(100007L);
                trade5.setVersion(1);
                trade5.setBook(book1);
                trade5.setCounterparty(counterparty2);
                trade5.setUtiCode("UTI-007");
                trade5.setTraderUser(traderUser);
                trade5.setTradeInputterUser(inputterUser);
                trade5.setTradeStatus(tradeStatus);
                trade5.setTradeDate(LocalDate.of(2025, 2, 4));
                trade5.setActive(true);
                trade5.setTradeLegs(tradeLegs1);

                Trade trade6 = new Trade();
                trade6.setId(1006L);
                trade6.setTradeId(100008L);
                trade6.setVersion(1);
                trade6.setBook(book2);
                trade6.setCounterparty(counterparty2);
                trade6.setUtiCode("UTI-008");
                trade6.setTraderUser(traderUser);
                trade6.setTradeInputterUser(inputterUser);
                trade6.setTradeStatus(tradeStatus);
                trade6.setTradeDate(LocalDate.of(2025, 3, 30));
                trade6.setActive(true);
                trade6.setTradeLegs(tradeLegs2);

                tradeLeg1.setTrade(trade1);
                tradeLeg2.setTrade(trade1);

                applicationUserRepository.saveAll(List.of(traderUser, inputterUser));
                tradeRepository.saveAll(List.of(trade1, trade2, trade3, trade4, trade5, trade6));

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

}
