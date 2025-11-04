package com.technicalchallenge.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.technicalchallenge.dto.TradeSummaryDTO;
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

    public DashboardViewService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
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
        Page<TradeSummaryDTO.PersonalView> personalView = tradeRepository.findPersonalTradesView(username, pageable);

        Object result = tradeRepository.findResultsOfTotals(username);
        Object[] totals = (Object[]) result;

        // Total amount of trades and notionals
        Long tradeCount = ((Number) totals[0]).longValue();
        BigDecimal totalNotional = (BigDecimal) totals[1];

        // Personalised projection view
        return new TradeSummaryDTO("Your Personal Trading View", username,
                tradeCount,
                totalNotional, personalView, null, null,
                null,
                null);

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

        // Total notional amounts by currency
        Map<String, BigDecimal> totalNotionalByCurrency = totalTrades.stream().flatMap(trade -> trade
                .getTradeLegs().stream()).filter(leg -> leg.getCurrency() != null && leg.getNotional() != null)
                .collect(Collectors.groupingBy(leg -> leg.getCurrency().getCurrency(),
                        Collectors.reducing(BigDecimal.ZERO, TradeLeg::getNotional, BigDecimal::add)));

        // Total number of trades by status
        Map<String, Long> totalTradeCountByStatus = totalTrades.stream()
                .collect(
                        Collectors.groupingBy(trade -> trade.getTradeStatus().getTradeStatus(), Collectors.counting()));

        // Breakdown by counterparty and trade type
        List<TradeSummaryDTO.Breakdown> byBreakdown = tradeRepository.findByBreakdown(username);

        // Risk exposure summaries - PayRecieve
        List<TradeSummaryDTO.RiskExposure> riskExposure = tradeRepository.findRiskExposure(username);

        return new TradeSummaryDTO("Trade Portfolio Summaries",
                username, null, null, null, totalNotionalByCurrency, totalTradeCountByStatus, byBreakdown,
                riskExposure);

    }

}
