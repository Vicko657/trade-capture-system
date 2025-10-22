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

        tradeLeg1.setTrade(trade1);
        tradeLeg2.setTrade(trade1);

        applicationUserRepository.saveAll(List.of(traderUser, inputterUser));
        tradeRepository.saveAll(List.of(trade1, trade2));

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
                .andExpect(jsonPath("$.length()").value(2));
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

}
