package com.technicalchallenge.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.data.domain.Page;

import lombok.Data;

// Class Based Projection
@Data
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

        // Breakdowns
        private final Map<String, BigDecimal> notionalByTradeType;
        private final Map<String, BigDecimal> notionalByCounterparty;

        // Risk Exposure
        private final Map<String, BigDecimal> riskExposure;

        public TradeSummaryDTO(String dashboard, String traderUsername, Long tradeCount, BigDecimal totalNotional,
                        Page<PersonalView> trades, Map<String, BigDecimal> totalNotionalByCurrency,
                        Map<String, Long> totalCountByStatus,
                        Map<String, BigDecimal> notionalByTradeType, Map<String, BigDecimal> notionalByCounterparty,
                        Map<String, BigDecimal> riskExposure) {

                this.dashBoard = dashboard;
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

        // Flatten Version of trades
        public static record PersonalView(String traderFullName,
                        Long tradeId, LocalDate tradeDate,
                        LocalDate tradeExecutionDate,
                        String tradeType,
                        String utiCode,
                        String tradeStatus, String bookName, String counterpartyName,
                        Integer version) {
        }

}
