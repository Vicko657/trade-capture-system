package com.technicalchallenge.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.technicalchallenge.dto.DailySummaryDTO;
import com.technicalchallenge.dto.TradeSummaryDTO;
import com.technicalchallenge.dto.DailySummaryDTO.Comparison;
import com.technicalchallenge.dto.DailySummaryDTO.Metrics;
import com.technicalchallenge.exceptions.DashboardDataNotFoundException;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.model.TradeLeg;
import com.technicalchallenge.repository.TradeRepository;

/**
 * Dashboard service class provides business logic and operations relating to
 * the dashboard views.
 * 
 * <p>
 * Has personalized dashboard views and summary statistics so that users can
 * monitor their
 * positions and make informed trading decisions.
 * </p>
 */
@Service
@Transactional(readOnly = true)
public class DashboardViewService {

        private final TradeRepository tradeRepository;
        private final BookService bookService;

        public DashboardViewService(TradeRepository tradeRepository, BookService bookService) {
                this.tradeRepository = tradeRepository;
                this.bookService = bookService;
        }

        /**
         * Dashboard View: Trader's personal trades
         * 
         * <p>
         * Projected view of the trader's personal trades
         * </p>
         * 
         * @param username   users authorized username
         * @param pagination users can select the page
         * @param sortBy     users can sort through the columns
         */
        public TradeSummaryDTO getTraderDashboard(String username, Pageable pageable) {

                // Current user's trading view
                Page<TradeSummaryDTO.PersonalView> personalView = tradeRepository.findPersonalTradesView(username,
                                pageable);

                Object result = tradeRepository.findResultsOfTotals(username);

                // DashboardDataNotFoundException thrown if the user doesn't have trades
                if (personalView == null || personalView.isEmpty() || result == null) {
                        throw new DashboardDataNotFoundException("Dashboard data was not found for " + username);
                }

                Object[] totals = (Object[]) result;

                // Total amount of trades and notionals
                Long tradeCount = ((Number) totals[0]).longValue();
                BigDecimal totalNotional = (BigDecimal) totals[1];

                // Personalised projection view
                TradeSummaryDTO blotterView = new TradeSummaryDTO("Your Personal Trading View", username,
                                tradeCount,
                                totalNotional, personalView, null, null, null,
                                null,
                                null);

                return blotterView;

        }

        /**
         * Dashboard View: Trade portfolio summaries
         * 
         * <p>
         * Projected view of the trade portfolio summaries.
         * </p>
         *
         * @param username users authorized username
         */

        public TradeSummaryDTO getTradePortfolioSummaries(String username) {

                List<Trade> totalTrades = tradeRepository.findAllTrades(username);

                // DashboardDataNotFoundException thrown if the user doesn't have trades
                if (totalTrades == null || totalTrades.isEmpty()) {
                        throw new DashboardDataNotFoundException("Dashboard data was not found for " + username);
                }

                // Total notional amounts by currency
                Map<String, BigDecimal> totalNotionalByCurrency = totalTrades.stream().flatMap(trade -> trade
                                .getTradeLegs().stream())
                                .filter(leg -> leg.getCurrency() != null && leg.getNotional() != null)
                                .collect(Collectors.groupingBy(leg -> leg.getCurrency().getCurrency(),
                                                Collectors.reducing(BigDecimal.ZERO, TradeLeg::getNotional,
                                                                BigDecimal::add)));

                // Total number of trades by status
                Map<String, Long> totalTradeCountByStatus = totalTrades.stream()
                                .collect(Collectors.groupingBy(trade -> trade.getTradeStatus().getTradeStatus(),
                                                Collectors.counting()));

                // Breakdown by trade type
                List<TradeSummaryDTO.TradeTypeBreakdown> byTradeType = tradeRepository
                                .findByTradeTypeBreakdown(username);

                // Breakdown by counterparty
                List<TradeSummaryDTO.CounterpartyBreakdown> byCounterparty = tradeRepository
                                .findByCounterpartyBreakdown(username);

                // Risk exposure summaries - PayRecieve
                List<TradeSummaryDTO.RiskExposure> riskExposure = tradeRepository.findRiskExposure(username);

                // DashboardDataNotFoundException thrown if the user doesn't have trades
                if (totalNotionalByCurrency == null || totalTradeCountByStatus == null
                                || byTradeType == null || byCounterparty == null
                                || riskExposure == null) {
                        throw new DashboardDataNotFoundException("Dashboard data was not found for " + username);
                }

                TradeSummaryDTO portfolioView = new TradeSummaryDTO("Trade Portfolio Summaries",
                                username, null, null, null, totalNotionalByCurrency, totalTradeCountByStatus,
                                byTradeType,
                                byCounterparty,
                                riskExposure);

                return portfolioView;

        }

        /**
         * Dashboard View: Book Level Activity
         * 
         * <p>
         * Projected view of book level activities.
         * </p>
         *
         * @param username users authorized username
         * @param bookId   book's unique identification
         */
        public DailySummaryDTO getBookLevelActivity(String username, Long bookId) {

                // Book level trading view
                List<DailySummaryDTO.BookActivity> bookView = tradeRepository.findBookLevelActivitySummary(username,
                                bookId);

                // DashboardDataNotFoundException thrown if the user doesn't have trades
                if (bookView == null || bookView.isEmpty()) {
                        throw new DashboardDataNotFoundException(
                                        "Dashboard data was not found for " + username + " with this " + bookId);
                }

                DailySummaryDTO bookActivityView = new DailySummaryDTO("Book Level Activities", null,
                                username, bookView, null, null);

                return bookActivityView;

        }

        /**
         * Dashboard View: Daily trading statistics
         * 
         * <p>
         * Projected view of daily trading statistics.
         * </p>
         *
         * @param username users authorized username
         */
        public DailySummaryDTO getDailyTradingStatistics(String username) {

                LocalDate todaysDate = LocalDate.now();
                LocalDate yesterdaysDate = todaysDate.minusDays(1);

                // Today's User's Trades
                List<Trade> todaysTrades = tradeRepository.findAllTrades(username).stream()
                                .filter(t -> t.getTradeDate().equals(todaysDate)).toList();

                // Previous Days - User's Trades
                List<Trade> yesterdaysTrades = tradeRepository.findAllTrades(username).stream()
                                .filter(t -> t.getTradeDate().equals(yesterdaysDate)).toList();

                // DashboardDataNotFoundException thrown if the user doesn't have trades
                if (todaysTrades == null || todaysTrades.isEmpty() || yesterdaysTrades == null
                                || yesterdaysTrades.isEmpty()) {
                        throw new DashboardDataNotFoundException("Dashboard data was not found for " + username);
                }

                // Daily trade count, total of notionals and user-specific performance metrics

                // Today's Summarised User's Trades
                DoubleSummaryStatistics todaysMetrics = todaysTrades.stream().collect(Collectors.summarizingDouble(
                                trade -> trade.getTradeLegs().stream().map(TradeLeg::getNotional)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                                .doubleValue()));

                // Yesterday's Summarised User's Trades
                DoubleSummaryStatistics yesterdaysMetrics = yesterdaysTrades.stream()
                                .collect(Collectors.summarizingDouble(
                                                trade -> trade.getTradeLegs().stream().map(TradeLeg::getNotional)
                                                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                                                .doubleValue()));

                Metrics todaysStats = new Metrics(todaysMetrics.getCount(), todaysMetrics.getAverage(),
                                todaysMetrics.getSum());
                Metrics yesterdaysStats = new Metrics(yesterdaysMetrics.getCount(), yesterdaysMetrics.getAverage(),
                                yesterdaysMetrics.getSum());

                Map<String, DailySummaryDTO.Metrics> metrics = new HashMap<>();
                metrics.put("todaysMetrics", todaysStats);
                metrics.put("yesterdaysMetrics", yesterdaysStats);

                // Comparison to previous trading days - difference in notioanls & percentage
                // change
                Double difference = todaysStats.totalNotional() - yesterdaysStats.totalNotional();

                Double percentageChange = yesterdaysStats.totalNotional() != 0
                                ? (difference / yesterdaysStats.totalNotional()) * 100
                                : 0.0;

                Comparison comparison = new Comparison(difference, percentageChange);
                Map<String, DailySummaryDTO.Comparison> comparisonOfMetrics = new HashMap<>();
                comparisonOfMetrics.put("notionalComparison", comparison);

                DailySummaryDTO dailyView = new DailySummaryDTO("Daily Trading Statistics", LocalDate.now(),
                                username,
                                null,
                                metrics, comparisonOfMetrics);

                return dailyView;

        }
}
