package com.technicalchallenge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.technicalchallenge.calculations.BigDecimalPercentages;
import com.technicalchallenge.dto.DailySummaryDTO;
import com.technicalchallenge.dto.TradeSummaryDTO;
import com.technicalchallenge.dto.DailySummaryDTO.BookActivity;
import com.technicalchallenge.dto.TradeSummaryDTO.CounterpartyBreakdown;
import com.technicalchallenge.dto.TradeSummaryDTO.PersonalView;
import com.technicalchallenge.dto.TradeSummaryDTO.RiskExposure;
import com.technicalchallenge.dto.TradeSummaryDTO.TradeTypeBreakdown;
import com.technicalchallenge.exceptions.DashboardDataNotFoundException;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.model.Currency;
import com.technicalchallenge.model.Index;
import com.technicalchallenge.model.LegType;
import com.technicalchallenge.model.PayRec;
import com.technicalchallenge.model.Schedule;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.model.TradeLeg;
import com.technicalchallenge.model.TradeStatus;
import com.technicalchallenge.repository.TradeRepository;

/**
 * Unit tests for {@link DashboardViewService}.
 *
 * <p>
 * Verifies the personal trading views and blotter systems logic.
 * Ensures that exception handling (such as
 * {@link DashboardDataNotFoundException}) work as expected.
 * <p>
 * The tests use mocked service behavior.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Dashboard Service Test Suite")
public class DashboardServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private BigDecimalPercentages bigDecimalPercentages;

    @InjectMocks
    private DashboardViewService dashboardViewService;

    private PersonalView personal;
    private TradeTypeBreakdown tradeTypeBreakdown;
    private CounterpartyBreakdown counterpartyBreakdown;
    private RiskExposure riskExposure1;
    private RiskExposure riskExposure2;
    private BookActivity bookActivity1;
    private BookActivity bookActivity2;
    private Trade trade1;
    private Trade trade2;
    private List<TradeLeg> tradeLegs1;
    private List<TradeLeg> tradeLegs2;
    private Book book;

    @BeforeEach
    void setUp() {

        // Application User Reference
        ApplicationUser tradeUser = new ApplicationUser();
        tradeUser.setActive(true);
        tradeUser.setId(1006L);
        tradeUser.setFirstName("John");
        tradeUser.setLastName("Smith");
        tradeUser.setLoginId("john");

        // Currency Reference
        Currency currency = new Currency();
        currency.setId(1000L);
        currency.setCurrency("EUR");

        // Tradestatus Reference
        TradeStatus status = new TradeStatus();
        status.setId(1000L);
        status.setTradeStatus("NEW");

        // PayRec Reference
        PayRec pay = new PayRec();
        pay.setId(1000L);
        pay.setPayRec("Pay");

        PayRec rec = new PayRec();
        rec.setId(1001L);
        rec.setPayRec("Receive");

        // Index Reference
        Index index = new Index();
        index.setId(1000L);
        index.setIndex("LIBOR");

        // LegType Reference
        LegType fixed = new LegType();
        fixed.setId(1000L);
        fixed.setType("Fixed");

        LegType floating = new LegType();
        floating.setId(1000L);
        floating.setType("Floating");

        Schedule schedule = new Schedule();
        schedule.setId(1000L);
        schedule.setSchedule("Quarterly");

        // Book Reference
        book = new Book();
        book.setId(1000L);
        book.setBookName("TestBookA");

        // Trade Leg Reference
        TradeLeg leg1 = new TradeLeg();
        leg1.setLegId(1001L);
        leg1.setNotional(BigDecimal.valueOf(3000000));
        leg1.setRate(5.0);
        leg1.setIndex(null);
        leg1.setPayReceiveFlag(pay);
        leg1.setCurrency(currency);
        leg1.setLegRateType(fixed);
        leg1.setCalculationPeriodSchedule(schedule);

        TradeLeg leg2 = new TradeLeg();
        leg2.setLegId(1002L);
        leg2.setNotional(BigDecimal.valueOf(2000000));
        leg2.setPayReceiveFlag(rec);
        leg2.setIndex(index);
        leg2.setCurrency(currency);
        leg2.setLegRateType(floating);
        leg2.setCalculationPeriodSchedule(schedule);

        TradeLeg leg3 = new TradeLeg();
        leg3.setLegId(1003L);
        leg3.setNotional(BigDecimal.valueOf(10000000));
        leg3.setRate(3.5);
        leg3.setIndex(null);
        leg3.setPayReceiveFlag(pay);
        leg3.setCurrency(currency);
        leg3.setLegRateType(fixed);
        leg3.setCalculationPeriodSchedule(schedule);

        TradeLeg leg4 = new TradeLeg();
        leg4.setLegId(1004L);
        leg4.setNotional(BigDecimal.valueOf(4000000));
        leg4.setPayReceiveFlag(rec);
        leg4.setIndex(index);
        leg4.setCurrency(currency);
        leg4.setLegRateType(floating);
        leg4.setCalculationPeriodSchedule(schedule);

        tradeLegs1 = List.of(leg1, leg2);
        tradeLegs2 = List.of(leg3, leg4);

        // Trade Reference
        trade1 = new Trade();
        trade1.setId(1009L);
        trade1.setTradeId(100009L);
        trade1.setTradeDate(LocalDate.of(2026, 5, 17));
        trade1.setTradeLegs(tradeLegs1);
        trade1.setTradeStatus(status);
        trade1.setBook(book);
        trade1.setTraderUser(tradeUser);

        trade2 = new Trade();
        trade2.setId(1009L);
        trade2.setTradeId(100009L);
        trade2.setTradeDate(LocalDate.of(2026, 5, 17));
        trade2.setTradeLegs(tradeLegs2);
        trade2.setTradeStatus(status);
        trade2.setBook(book);
        trade2.setTraderUser(tradeUser);

        // Nested Projection DTOs for TradeSummayDTO and DailySummaryDTO
        personal = new PersonalView("John Smith", 1001L,
                LocalDate.of(2026, 5,
                        17),
                LocalDate.of(2025, 5,
                        17),
                "SWAP", "uti-377238",
                "NEW", "TestBookA", "TestCounterpartyA",
                1);

        tradeTypeBreakdown = new TradeTypeBreakdown("Option", BigDecimal.valueOf(2000000), BigDecimal.valueOf(20.0));

        counterpartyBreakdown = new CounterpartyBreakdown("TestcounterpartyA", BigDecimal.valueOf(4000000),
                BigDecimal.valueOf(40.0));

        riskExposure1 = new RiskExposure(1001L, 0.05, "FX", "EUR", "Pay", BigDecimal.valueOf(1000000));

        riskExposure2 = new RiskExposure(1002L, 0.0, "FX", "USD", "Recieve", BigDecimal.valueOf(1000000));

        bookActivity1 = new BookActivity(trade1.getBook().getBookName(), "NY Trading", "FX Options",
                BigDecimal.valueOf(4000000), 1);
        bookActivity2 = new BookActivity(trade2.getBook().getBookName(), "NY Trading", "FX Options",
                BigDecimal.valueOf(6000000), 1);

    }

    /**
     * Tests if viewing personal trades is successful
     */
    @Test
    @DisplayName("FindTradersPersonalView: Finds trader's data for the personal view")
    void testFindTradersPersonalView_Success() {

        // Given - Trader's username, pagaeable, object of totals, page and mocked stubs
        String username = "john";
        Pageable pageable = PageRequest.of(0, 4);
        Long tradeCount = 1L;
        BigDecimal totalNotional = BigDecimal.valueOf(1000000);

        Object totals = new Object[] { tradeCount, totalNotional };
        Page<PersonalView> mockPage = new PageImpl<>(List.of(personal));

        when(tradeRepository.findResultsOfTotals(any())).thenReturn(totals);
        when(tradeRepository.findPersonalTradesView(any(), any(Pageable.class))).thenReturn(mockPage);

        // When - getTraderDashboard method call
        TradeSummaryDTO result = dashboardViewService.getTraderDashboard(username, pageable);

        // Then - Verified results match expected personal view
        assertNotNull(result);
        assertEquals(1L, result.getTradeCount());
        assertEquals(1, result.getTrades().getContent().size());
        assertEquals("TestBookA", result.getTrades().getContent().get(0).bookName());

    }

    /**
     * Tests if viewing personal trades throws a exception
     */
    @Test
    @DisplayName("FindTradersPersonalView: Data for the personal view is not found")
    void testFindTradersPersonalViewNotFound_Failed() throws Exception {

        // Given - Trader's username and pagaeable
        String username = "john";
        Pageable pageable = PageRequest.of(0, 4);

        // When - A DashboardDataNotFoundException is thrown and assertThrows returns
        // the exceptions
        DashboardDataNotFoundException exception = assertThrows(DashboardDataNotFoundException.class, () -> {
            dashboardViewService.getTraderDashboard(username, pageable);
        });

        // Then - Verifies the exception was thrown.
        assertEquals("Dashboard data was not found for " + username, exception.getMessage());

    }

    /**
     * Tests if viewing portfolio summary is successful
     */
    @Test
    @DisplayName("FindTradersPortfolioSummary: Finds trader's data for the portfolio summary view")
    void testFindTradersPortfolioSummaryView_Success() {

        // Given - Trader's username and mocked stubs
        String username = "john";

        when(tradeRepository.findAllTrades(any())).thenReturn(List.of(trade1, trade2));
        when(tradeRepository.findByTradeTypeBreakdown(any())).thenReturn(List.of(tradeTypeBreakdown));
        when(tradeRepository.findByCounterpartyBreakdown(any())).thenReturn(List.of(counterpartyBreakdown));
        when(tradeRepository.findRiskExposure(any())).thenReturn(List.of(riskExposure1, riskExposure2));

        // When - getTradePortfolioSummaries method call
        TradeSummaryDTO result = dashboardViewService.getTradePortfolioSummaries(username);
        Map<String, BigDecimal> notionalMap = result.getTotalNotionalByCurrency();
        Map<String, Long> tradeMap = result.getTotalCountByStatus();

        // Then - Verified results match expected portfolio view
        assertNotNull(result);
        assertTrue(notionalMap.containsKey("EUR"));
        assertTrue(tradeMap.containsValue(2L));
        assertEquals(2, result.getRiskExposure().size());
        assertEquals(BigDecimal.valueOf(2000000),
                result.getNotionalByTradeType().get(0).totalNotional());

    }

    /**
     * Tests if viewing portfolio summary throws a exception
     */
    @Test
    @DisplayName("FindTradersPortfolioSummary: Data for the portfolio summary is not found")
    void testFindTradersPortfolioSummaryViewNotFound_Failed() throws Exception {

        // Given - Trader's username
        String username = "john";

        // When - A DashboardDataNotFoundException is thrown and assertThrows returns
        // the exceptions
        DashboardDataNotFoundException exception = assertThrows(DashboardDataNotFoundException.class, () -> {
            dashboardViewService.getTradePortfolioSummaries(username);
        });

        // Then - Verifies the exception was thrown.
        assertEquals("Dashboard data was not found for " + username, exception.getMessage());

    }

    /**
     * Tests if viewing book level activities is successful
     */
    @Test
    @DisplayName("FindTradersBookLevelActivities: Finds trader's data for the book level activities view")
    void testFindTradersBookLevelActivitiesView_Success() {

        // Given - Trader's username, bookId and mocked stubs
        String username = "john";
        Long bookId = trade1.getBook().getId();

        when(tradeRepository.findBookLevelActivitySummary(any(), any()))
                .thenReturn(List.of(bookActivity1, bookActivity2));

        // When - getBookLevelActivity method call
        DailySummaryDTO result = dashboardViewService.getBookLevelActivity(username, bookId);

        // Then - Verified results match expected book level view
        assertNotNull(result);
        assertEquals(2, result.getBookActivites().size());
        assertEquals("FX Options", result.getBookActivites().get(0).subDeskName());

    }

    /**
     * Tests if viewing book level activities throws a exception
     */
    @Test
    @DisplayName("FindTradersBookLevelActivities: Data for the book level activites is not found")
    void testFindTradersBookLevelActivitiesNotFound_Failed() throws Exception {

        // Given - Trader's username and bookId
        String username = "john";
        Long bookId = 1067L;

        // When - A DashboardDataNotFoundException is thrown and assertThrows returns
        // the exceptions
        DashboardDataNotFoundException exception = assertThrows(DashboardDataNotFoundException.class, () -> {
            dashboardViewService.getBookLevelActivity(username, bookId);
        });

        // Then - Verifies the exception was thrown.
        assertEquals("Dashboard data was not found for " + username + " with this " + bookId, exception.getMessage());

    }

    /**
     * Tests if viewing daily trading statistics is successful
     */
    @Test
    @DisplayName("FindTradersDailyTradingStatistics: Finds trader's data for the daily trading statistics view")
    void testFindTradersDailyTradingStatisticsView_Success() {

        // Given - Trader's username, dates and mocked stubs
        String username = "john";
        LocalDate todaysDate = LocalDate.now();
        LocalDate yesterdaysDate = todaysDate.minusDays(1);
        trade1.setTradeDate(todaysDate);
        trade2.setTradeDate(yesterdaysDate);

        when(tradeRepository.findAllTrades(any())).thenReturn(List.of(trade1, trade2));

        when(bigDecimalPercentages.toPercentageOf(new BigDecimal(
                14000000),
                new BigDecimal(
                        -9000000)))
                .thenReturn(new BigDecimal("-64.29"));

        // When - getDailyTradingStatistics method call including metric and comparison
        // results
        DailySummaryDTO result = dashboardViewService.getDailyTradingStatistics(username);
        DailySummaryDTO.Metrics metricsResult = result.getSummarisedMetrics().get("todaysMetrics");
        DailySummaryDTO.Comparison comparisonResult = result.getComparison().get("notionalComparison");

        // Then - Verified results match expected daily trading statistics view
        assertNotNull(result);
        assertTrue(result.getTodaysDate().isEqual(LocalDate.now()));
        assertEquals(new BigDecimal(2500000), metricsResult.averageNotional());
        assertEquals(2L, metricsResult.tradeCount());
        assertEquals(new BigDecimal(-9000000), comparisonResult.difference());
        assertEquals(new BigDecimal("-64.29"), comparisonResult.percentageChange());

    }

    /**
     * Tests if viewing daily trading statistics throws a exception
     */
    @Test
    @DisplayName("FindTradersDailyTradingStatistics: Data for the daily trading statictics is not found")
    void testFindTradersDailyTradingStatisticsNotFound_Failed() throws Exception {

        // Given - Trader's username
        String username = "john";

        // When - A DashboardDataNotFoundException is thrown and assertThrows returns
        // the exceptions
        DashboardDataNotFoundException exception = assertThrows(DashboardDataNotFoundException.class, () -> {
            dashboardViewService.getDailyTradingStatistics(username);
        });

        // Then - Verifies the exception was thrown.
        assertEquals("Dashboard data was not found for " + username, exception.getMessage());

    }

}
