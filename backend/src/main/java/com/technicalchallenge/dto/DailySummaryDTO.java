package com.technicalchallenge.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Class Based Projection used to structure the data objects
 * for the dashboards
 * 
 * 
 * With aggerated functions in the {@link TradeRepository},
 * lists and maps in {@link DashboardViewService}
 * 
 * 
 * Used for:
 * Book Level Activity Summary
 * Daily Trading Statitics Summary
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Used to create dynamic views for the dashboards")
public class DailySummaryDTO {

        @Schema(description = "Name of Dashboard", example = "Daily Trading Statitics")
        private final String dashBoardName;

        @Schema(description = "Trader's username, same as loginId", example = "victoria")
        private final String traderUsername;

        @Schema(description = "Today's date", example = "7-11-2025")
        private final LocalDate todaysDate;

        // Summarised List of Book Activities
        @Schema(description = "Book-level activity summaries")
        private final List<BookActivity> bookActivites;

        // Summarised Map of Book Activities
        @Schema(description = "User-specific performance metrics")
        private final Map<String, Metrics> summarisedMetrics;

        // Summarised Map of Book Activities
        @Schema(description = "Comparison to previous trading days")
        private final Map<String, Comparison> comparison;

        // Contstructor
        public DailySummaryDTO(String dashboardName, LocalDate todaysDate, String traderUsername,
                        List<BookActivity> bookActivites,
                        Map<String, Metrics> summarisedMetrics, Map<String, Comparison> comparison) {
                this.dashBoardName = dashboardName.toUpperCase();
                this.todaysDate = todaysDate;
                this.traderUsername = traderUsername.toUpperCase();
                this.bookActivites = bookActivites;
                this.summarisedMetrics = summarisedMetrics;
                this.comparison = comparison;
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
         * Future: Would implement more comparisions and metrics into the views
         * 
         */

        // Flatten Version of books
        @Schema(description = "Book Activity")
        public static record BookActivity(
                        String bookName,
                        String costCenterName, String subDeskName,
                        BigDecimal totalNotional, Integer version) {
        }

        // Flatten Version of metrics
        @Schema(description = "User Metrics")
        public static record Metrics(
                        Long tradeCount,
                        Double averageNotional,
                        Double totalNotional) {
        }

        // Flatten Version of comparison
        @Schema(description = "Date comparisons")
        public static record Comparison(
                        Double difference,
                        Double percentageChange) {
        }

}
