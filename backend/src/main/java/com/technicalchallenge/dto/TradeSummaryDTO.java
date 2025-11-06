package com.technicalchallenge.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

// Class Based Projection
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TradeSummaryDTO {

        // Name of Dashboard
        private final String dashBoard;

        // User (Current)
        private final String traderUsername;

        // Personalised View
        private final Long tradeCount;
        private final BigDecimal totalNotional;
        private final Page<PersonalView> trades;

        // Calculation of Fields
        private final Map<String, BigDecimal> totalNotionalByCurrency;
        private final Map<String, Long> totalCountByStatus;

        // The Breakdowns
        private final List<TradeTypeBreakdown> notionalByTradeType;
        private final List<CounterpartyBreakdown> notionalByCounterparty;

        // Risk Exposure
        private final List<RiskExposure> riskExposure;

        public TradeSummaryDTO(String dashboard, String traderUsername, Long tradeCount, BigDecimal totalNotional,
                        Page<PersonalView> trades, Map<String, BigDecimal> totalNotionalByCurrency,
                        Map<String, Long> totalCountByStatus,
                        List<TradeTypeBreakdown> notionalByTradeType,
                        List<CounterpartyBreakdown> notionalByCounterparty,
                        List<RiskExposure> riskExposure) {

                this.dashBoard = dashboard.toUpperCase();
                this.traderUsername = traderUsername.toUpperCase();
                this.tradeCount = tradeCount;
                this.totalNotional = totalNotional;
                this.trades = trades;
                this.totalNotionalByCurrency = totalNotionalByCurrency;
                this.totalCountByStatus = totalCountByStatus;
                this.notionalByTradeType = notionalByTradeType;
                this.notionalByCounterparty = notionalByCounterparty;
                this.riskExposure = riskExposure;

        }

        // Flatten Version of trades
        public static record PersonalView(String traderFullName,
                        Long tradeId, LocalDate tradeDate,
                        LocalDate tradeExecutionDate,
                        String tradeType,
                        String utiCode,
                        String tradeStatus, String bookName, String counterpartyName,
                        Integer version) {
        }

        // Flatten Version of breakdownByTradeType
        public static record TradeTypeBreakdown(
                        String tradeType,
                        BigDecimal totalNotional, Double percentage) {
        }

        // Flatten Version of breakdownByCounterparty
        public static record CounterpartyBreakdown(
                        String counterpartyName,
                        BigDecimal totalNotional,
                        Double percentage) {

        }

        // Flatten Version of riskexposure
        public static record RiskExposure(Long tradeLegId, Double rate, String deskName,
                        String payCurrency,
                        String payRecieveFlag,
                        BigDecimal totalNotional) {
        }

}
