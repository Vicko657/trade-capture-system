package com.technicalchallenge.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.technicalchallenge.dto.DailySummaryDTO.*;
import com.technicalchallenge.dto.TradeSummaryDTO.*;
import com.technicalchallenge.dto.DailySummaryDTO;
import com.technicalchallenge.dto.TradeSummaryDTO;
import com.technicalchallenge.model.*;
import com.technicalchallenge.model.Currency;
import com.technicalchallenge.security.ApplicationUserDetails;
import com.technicalchallenge.service.DashboardViewService;

/**
 * Unit tests for {@link DashboardViewController}.
 *
 * <p>
 * Verifies the API endpoints recieve and process HTTP response codes like 200
 * OK or 204 NO CONTENT
 *
 * <p>
 * The tests use mocked controller behavior.
 */
@WebMvcTest(DashboardViewController.class)
@DisplayName(value = "Dashboard Controller Test Suite")
public class DashboardViewControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private DashboardViewService dashboardViewService;

        @InjectMocks
        private ApplicationUserDetails userDetails;

        private PersonalView personal1;
        private PersonalView personal2;
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
        private ApplicationUser tradeUser;
        private Comparison comparison;
        private Metrics metrics;

        @BeforeEach
        void setUp() {

                // Privilege Reference
                Privilege privilege = new Privilege();
                privilege.setName("DASHBOARD_VIEW");

                // UserPrivilege Reference
                UserPrivilege userPrivilege = new UserPrivilege();
                userPrivilege.setPrivilege(privilege);

                // Counterparty Reference
                Counterparty counterparty = new Counterparty();
                counterparty.setName("TestCounterpartyName");

                // TradeType Reference
                TradeType tradeType = new TradeType();
                tradeType.setTradeType("Swap");

                // UserProfile Reference
                UserProfile userProfile = new UserProfile();
                userProfile.setUserType("TRADER");
                userProfile.setPrivileges(List.of(userPrivilege));

                // Application User Reference
                tradeUser = new ApplicationUser();
                tradeUser.setId(1006L);
                tradeUser.setFirstName("Victoria");
                tradeUser.setLastName("Olusegun");
                tradeUser.setLoginId("victoria");
                tradeUser.setPassword("password");
                tradeUser.setActive(true);
                tradeUser.setUserProfile(userProfile);

                // Application User Details Reference
                userDetails = new ApplicationUserDetails(tradeUser);

                // Currency Reference
                Currency currency = new Currency();
                currency.setCurrency("EUR");

                // Tradestatus Reference
                TradeStatus status = new TradeStatus();
                status.setTradeStatus("NEW");

                // Book Reference
                book = new Book();
                book.setId(1000L);
                book.setBookName("TestBookA");

                // Trade Leg Reference
                TradeLeg leg1 = new TradeLeg();
                leg1.setLegId(1L);
                leg1.setNotional(BigDecimal.valueOf(2000000));
                leg1.setRate(0.05);
                leg1.setCurrency(currency);

                TradeLeg leg2 = new TradeLeg();
                leg2.setLegId(2L);
                leg2.setNotional(BigDecimal.valueOf(6000000));
                leg2.setRate(0.0);
                leg2.setCurrency(currency);

                TradeLeg leg3 = new TradeLeg();
                leg3.setLegId(3L);
                leg3.setNotional(BigDecimal.valueOf(6000000));
                leg3.setRate(0.05);
                leg3.setCurrency(currency);

                TradeLeg leg4 = new TradeLeg();
                leg4.setLegId(4L);
                leg4.setNotional(BigDecimal.valueOf(4000000));
                leg4.setRate(0.0);
                leg4.setCurrency(currency);

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
                trade1.setTradeType(tradeType);
                trade1.setCounterparty(counterparty);
                trade1.setTradeExecutionDate(LocalDate.of(2026, 5, 17));

                trade2 = new Trade();
                trade2.setId(1009L);
                trade2.setTradeId(100009L);
                trade2.setTradeDate(LocalDate.of(2026, 5, 17));
                trade2.setTradeLegs(tradeLegs2);
                trade2.setTradeStatus(status);
                trade2.setBook(book);
                trade2.setTraderUser(tradeUser);
                trade2.setTradeType(tradeType);
                trade2.setCounterparty(counterparty);
                trade2.setTradeExecutionDate(LocalDate.of(2026, 5, 17));

                // Mocked DTO Projections
                personal1 = new PersonalView(
                                trade1.getTraderUser().getFirstName() + trade1.getTraderUser().getLastName(),
                                trade1.getId(), trade1.getTradeDate(), trade1.getTradeExecutionDate(),
                                trade1.getTradeType().getTradeType(), trade1.getUtiCode(),
                                trade1.getTradeStatus().getTradeStatus(),
                                trade1.getBook().getBookName(), trade1.getCounterparty().getName(),
                                trade1.getVersion());

                personal2 = new PersonalView(
                                trade2.getTraderUser().getFirstName() + trade2.getTraderUser().getLastName(),
                                trade2.getId(), trade2.getTradeDate(), trade2.getTradeExecutionDate(),
                                trade2.getTradeType().getTradeType(), trade2.getUtiCode(),
                                trade2.getTradeStatus().getTradeStatus(),
                                trade2.getBook().getBookName(), trade2.getCounterparty().getName(),
                                trade2.getVersion());

                tradeTypeBreakdown = new TradeTypeBreakdown("Option", BigDecimal.valueOf(2000000),
                                BigDecimal.valueOf(20.0));

                counterpartyBreakdown = new CounterpartyBreakdown("TestcounterpartyA", BigDecimal.valueOf(4000000),
                                BigDecimal.valueOf(40.0));

                riskExposure1 = new RiskExposure(1001L, 0.05, "FX", "EUR", "Pay", BigDecimal.valueOf(1000000));

                riskExposure2 = new RiskExposure(1002L, 0.0, "FX", "USD", "Recieve", BigDecimal.valueOf(1000000));

                bookActivity1 = new BookActivity(trade1.getBook().getBookName(), "NY Trading", "FX Options",
                                BigDecimal.valueOf(4000000), 1);
                bookActivity2 = new BookActivity(trade2.getBook().getBookName(), "NY Trading", "FX Options",
                                BigDecimal.valueOf(6000000), 1);

                comparison = new Comparison(BigDecimal.valueOf(-2000000), BigDecimal.valueOf(40.0));
                metrics = new Metrics(2L, BigDecimal.valueOf(300000), BigDecimal.valueOf(45000000));

                // Mocked User Authentication for Test (Spring Security)
                ApplicationUserDetails userDetails = new ApplicationUserDetails(tradeUser);
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

        }

        /**
         * Tests expected response code when the trader views their personal view
         */
        @Test
        @WithMockUser(username = "victoria", roles = "TRADER")
        @DisplayName("GetTradersDashboard: 200 OK Response")
        void testGetTradersDashboard() throws Exception {

                // Given - Mocked authorized user, service, entities and page with a list of
                // trades
                String username = tradeUser.getLoginId();
                Pageable pageable = PageRequest.of(0, 4);
                Long tradeCount = 2L;
                BigDecimal totalNotional = BigDecimal.valueOf(3000000.0);
                Page<PersonalView> trades = new PageImpl<>(List.of(personal1, personal2), pageable, 1);

                TradeSummaryDTO personalView = new TradeSummaryDTO(
                                "Your Personal Trading View", username, tradeCount,
                                totalNotional, trades,
                                null, null, null, null,
                                null);

                when(dashboardViewService.getTraderDashboard(username, pageable)).thenReturn(personalView);

                // When/Then - set up a GET request to a test endpoint - tests were successful
                mockMvc.perform(get("/api/trades/dashboard/my-trades")
                                .param("page", "0")
                                .param("size", "4")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalNotional").value(3000000.0))
                                .andExpect(jsonPath("$.tradeCount").value(2))
                                .andExpect(jsonPath("$.traderUsername").value("victoria"))
                                .andExpect(jsonPath("$.dashboard").value(
                                                "Your Personal Trading View"))
                                .andExpect(jsonPath("$.trades.content[0].tradeExecutionDate").value(
                                                LocalDate.of(2026, 05, 17).toString()));
                // Verifies the search happened once
                verify(dashboardViewService).getTraderDashboard(any(), any());
        }

        /**
         * Tests expected response code when the personal view returns empty dto
         */
        @Test
        @WithMockUser(username = "victoria", roles = "TRADER")
        @DisplayName("GetTradersDashboardNoContent: 204 NO CONTENT Response")
        void testGetTradersDashboardNoContent() throws Exception {

                // Given - Mocked service and authorized user returns empty dto
                String username = tradeUser.getLoginId();

                TradeSummaryDTO personalView = new TradeSummaryDTO(
                                "Your Personal Trading View", username, null, null, null, null, null, null, null,
                                null);

                when(dashboardViewService.getTraderDashboard(any(), any())).thenReturn(personalView);

                // When/Then - set up a GET request to a test endpoint - tests were successful
                mockMvc.perform(get("/api/trades/dashboard/my-trades"))
                                .andExpect(status().isNoContent());

                // Verifies the search happened once
                verify(dashboardViewService).getTraderDashboard(any(), any());
        }

        /**
         * Tests expected response code when the trader views their portfolio view
         */
        @Test
        @WithMockUser(username = "victoria", roles = "TRADER")
        @DisplayName("GetPortfolioSummaries: 200 OK Response")
        void testGetPortfolioSummaries() throws Exception {

                // Given - Mocked authorized user, service, entities and maps with a list of
                // trades
                String username = tradeUser.getLoginId();

                Map<String, BigDecimal> totalNotionalByCurrency = new HashMap<>();
                totalNotionalByCurrency.put("totalNotional", BigDecimal.valueOf(3000000.0));

                Map<String, Long> totalCountByStatus = new HashMap<>();
                totalCountByStatus.put("totalCount", 2L);

                TradeSummaryDTO portfolioSummaryView = new TradeSummaryDTO("Trade Portfolio Summaries", username, null,
                                null,
                                null,
                                totalNotionalByCurrency,
                                totalCountByStatus,
                                List.of(tradeTypeBreakdown), List.of(counterpartyBreakdown),
                                List.of(riskExposure1, riskExposure2));

                when(dashboardViewService.getTradePortfolioSummaries(username)).thenReturn(portfolioSummaryView);

                // When/Then - set up a GET request to a test endpoint - tests were successful
                mockMvc.perform(get("/api/trades/dashboard/summary")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalNotionalByCurrency.totalNotional").value(3000000.0))
                                .andExpect(jsonPath("$.riskExposure[0].deskName").value("FX"))
                                .andExpect(jsonPath("$.traderUsername").value("victoria"))
                                .andExpect(jsonPath("$.notionalByCounterparty[0].percentage").value(
                                                40.0));

                // Verifies the search happened once
                verify(dashboardViewService).getTradePortfolioSummaries(any());
        }

        /**
         * Tests expected response code when the personal view returns empty dto
         */
        @Test
        @WithMockUser(username = "victoria", roles = "TRADER")
        @DisplayName("GetPortfolioSummariesNoContent: 204 NO CONTENT Response")
        void testGetPortfolioSummariesNoContent() throws Exception {

                // Given - Mocked authorized user, service returns empty dto
                String username = tradeUser.getLoginId();

                TradeSummaryDTO portfolioView = new TradeSummaryDTO(
                                "Trade Portfolio Summaries", username, null, null, null, null, null, null, null,
                                null);

                when(dashboardViewService.getTradePortfolioSummaries(any())).thenReturn(portfolioView);

                // When/Then - set up a GET request to a test endpoint - tests were successful
                mockMvc.perform(get("/api/trades/dashboard/summary"))
                                .andExpect(status().isNoContent());

                // Verifies the search happened once
                verify(dashboardViewService).getTradePortfolioSummaries(any());
        }

        /**
         * Tests expected response code when the trader views their
         * book level activity view
         */
        @Test
        @WithMockUser(username = "victoria", roles = "TRADER")
        @DisplayName("GetBookActivites: 200 OK Response")
        void testGetBookActivites() throws Exception {

                // Given - Mocked authorized user, service, entities and dto.
                String username = "victoria";
                Long id = trade1.getBook().getId();
                DailySummaryDTO bookActivityView = new DailySummaryDTO("Book Level Activities", null, username,
                                List.of(bookActivity1, bookActivity2), null,
                                null);

                when(dashboardViewService.getBookLevelActivity(username, id)).thenReturn(bookActivityView);

                // When/Then - set up a GET request to a test endpoint - tests were successful
                mockMvc.perform(get("/api/trades/dashboard/book/1000/trades")
                                .param("id", "1000L")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.bookActivites[0].costCenterName").value("NY Trading"))
                                .andExpect(jsonPath("$.bookActivites[0].bookName").value("TestBookA"))
                                .andExpect(jsonPath("$.bookActivites[0].subDeskName").value("FX Options"));

                verify(dashboardViewService).getBookLevelActivity(any(), any());
        }

        /**
         * Tests expected response code when the book activities view returns empty list
         */
        @Test
        @WithMockUser(username = "victoria", roles = "TRADER")
        @DisplayName("GetBookActivitesNoContent: 204 NO CONTENT Response")
        void testGetBookActivitesNoContent() throws Exception {

                // Given - Mocked authorized user, bookId and service returns empty dto
                String username = tradeUser.getLoginId();
                Long bookId = 1002L;

                DailySummaryDTO bookLevelActivitiesView = new DailySummaryDTO(
                                "Book Level Activities", null, username, null, null, null);

                when(dashboardViewService.getBookLevelActivity(any(), any())).thenReturn(bookLevelActivitiesView);

                // When/Then - set up a GET request to a test endpoint - tests were successful
                mockMvc.perform(get("/api/trades/dashboard/book/1002/trades")
                                .param("bookId", "null"))
                                .andExpect(status().isNoContent());

                // Verifies the search happened once
                verify(dashboardViewService).getBookLevelActivity(username, bookId);
        }

        /**
         * Tests expected response code when the trader views their daily summary view
         */
        @Test
        @WithMockUser(username = "victoria", roles = "TRADER")
        @DisplayName("GetDailyTradingStatistics: 200 OK Response")
        void testGetDailyTradingStatistics() throws Exception {

                // Given - Mocked authorized user, service, entities and maps
                String username = tradeUser.getLoginId();
                LocalDate today = LocalDate.of(2025, 11, 07);
                LocalDate yesterday = today.minusDays(1);

                trade1.setTradeDate(today);
                trade2.setTradeDate(yesterday);

                Map<String, Metrics> summerisedMetrics = new HashMap<>();
                summerisedMetrics.put("todaysMetrics", metrics);

                Map<String, Comparison> comparisons = new HashMap<>();
                comparisons.put("todaysComparison", comparison);

                DailySummaryDTO dailySummaryView = new DailySummaryDTO("Daily Trading Statistics", today, username,
                                null,
                                summerisedMetrics, comparisons);

                when(dashboardViewService.getDailyTradingStatistics(username)).thenReturn(dailySummaryView);

                // When/Then - set up a GET request to a test endpoint - tests were successful
                mockMvc.perform(get("/api/trades/dashboard/daily-summary")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.summarisedMetrics['todaysMetrics'].averageNotional")
                                                .value(Double.valueOf(
                                                                300000)))
                                .andExpect(jsonPath("$.comparison['todaysComparison'].difference").value(Double.valueOf(
                                                -2000000.0)))
                                .andExpect(jsonPath("$.todaysDate").value(trade1.getTradeDate().toString()));

                // Verifies the search happened once
                verify(dashboardViewService).getDailyTradingStatistics(any());
        }

        /**
         * Tests expected response code when the daily summary view returns empty list
         */
        @Test
        @WithMockUser(username = "victoria", roles = "TRADER")
        @DisplayName("GetDailyTradingStatisticsNoContent: 204 NO CONTENT Response")
        void testGetDailyTradingStatisticsNoContent() throws Exception {

                // Given - Mocked authorized user, date and service returns empty dto
                String username = tradeUser.getLoginId();
                LocalDate todaysDate = LocalDate.now();

                DailySummaryDTO dailySummariesView = new DailySummaryDTO(
                                "Daily Trading Statistics", todaysDate, username, null, null, null);

                when(dashboardViewService.getDailyTradingStatistics(any())).thenReturn(dailySummariesView);

                // When/Then - set up a GET request to a test endpoint - tests were successful
                mockMvc.perform(get("/api/trades/dashboard/daily-summary"))
                                .andExpect(status().isNoContent());

                // Verifies the search happened once
                verify(dashboardViewService).getDailyTradingStatistics(username);
        }

}
