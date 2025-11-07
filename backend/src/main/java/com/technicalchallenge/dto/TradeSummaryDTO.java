package com.technicalchallenge.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.technicalchallenge.repository.TradeRepository;
import com.technicalchallenge.service.DashboardViewService;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Class Based Projection used to structure the data objects for the dashboards
 * 
 * 
 * With aggerated functions in the {@link TradeRepository},
 * lists and maps in {@link DashboardViewService}
 * 
 * 
 * Used for:
 * Trader's Personal Blotter System
 * Trader's Portfolio Summary
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Used to create dynamic views for the dashboards")
public class TradeSummaryDTO {

        @Schema(description = "Name of Dashboard", example = "Trader's Portfoilo Summary")
        private final String dashboard;

        @Schema(description = "Trader's username, same as loginId", example = "simon")
        private final String traderUsername;

        // Personalised View
        @Schema(description = "Total amount of trades the user has", example = "10")
        private final Long tradeCount;
        @Schema(description = "Value of a asset (Hypothetical)", example = "50000000.0")
        private final BigDecimal totalNotional;
        @Schema(description = "Page of trade")
        private final Page<PersonalView> trades;

        // Calculation of Fields
        @Schema(description = "Total notional amounts by currency")
        private final Map<String, BigDecimal> totalNotionalByCurrency;
        @Schema(description = "Total number of trades by status")
        private final Map<String, Long> totalCountByStatus;

        // The Breakdowns
        @Schema(description = "Breakdown by trade type")
        private final List<TradeTypeBreakdown> notionalByTradeType;
        @Schema(description = "Breakdown by counterparty")
        private final List<CounterpartyBreakdown> notionalByCounterparty;

        // Risk Exposure
        @Schema(description = "Risk exposure summaries")
        private final List<RiskExposure> riskExposure;

        // Contstructor
        public TradeSummaryDTO(String dashboard, String traderUsername, Long tradeCount, BigDecimal totalNotional,
                        Page<PersonalView> trades, Map<String, BigDecimal> totalNotionalByCurrency,
                        Map<String, Long> totalCountByStatus,
                        List<TradeTypeBreakdown> notionalByTradeType,
                        List<CounterpartyBreakdown> notionalByCounterparty,
                        List<RiskExposure> riskExposure) {

                this.dashboard = dashboard;
                this.traderUsername = traderUsername;
                this.tradeCount = tradeCount;
                this.totalNotional = totalNotional;
                this.trades = trades;
                this.totalNotionalByCurrency = totalNotionalByCurrency;
                this.totalCountByStatus = totalCountByStatus;
                this.notionalByTradeType = notionalByTradeType;
                this.notionalByCounterparty = notionalByCounterparty;
                this.riskExposure = riskExposure;

        }

        /**
         * Nested Projections used to tailor and refine data
         * from the queries and maps
         * 
         * Removed audits and other data to help the trader
         * monitor their activity efficiently
         * 
         * 
         * 
         * Future: Would implement more risk exposures into the views and explore using
         * a Big Decimal plugin to create better calculations
         * 
         */

        // Flatten Version of trades
        @Schema(description = "Personal Trades")
        public static record PersonalView(String traderFullName,
                        Long tradeId, LocalDate tradeDate,
                        LocalDate tradeExecutionDate,
                        String tradeType,
                        String utiCode,
                        String tradeStatus, String bookName, String counterpartyName,
                        Integer version) {
        }

        // Flatten Version of breakdownByTradeType
        @Schema(description = "Breakdown by TradeType")
        public static record TradeTypeBreakdown(
                        String tradeType,
                        BigDecimal totalNotional, BigDecimal percentage) {
        }

        // Flatten Version of breakdownByCounterparty
        @Schema(description = "Breakdown By Counterparty")
        public static record CounterpartyBreakdown(
                        String counterpartyName,
                        BigDecimal totalNotional,
                        BigDecimal percentage) {

        }

        // Flatten Version of riskexposure
        @Schema(description = "Risk exposure")
        public static record RiskExposure(Long tradeLegId, Double rate, String deskName,
                        String payCurrency,
                        String payRecieveFlag,
                        BigDecimal totalNotional) {
        }

}
