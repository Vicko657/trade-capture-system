package com.technicalchallenge.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

// Class Based Projection
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DailySummaryDTO {

    // Name of Dashboard
    private final String dashBoardName;

    // User (Current)
    private final String traderUsername;

    // Today's date
    private final LocalDate todaysDate;

    // Summarised List of Book Activities
    private final List<BookActivity> bookActivites;

    // Summarised Metrics
    private final Map<String, Metrics> summerisedMetrics;

    // Comparison of Metrics
    private final Map<String, Comparison> comparison;

    public DailySummaryDTO(String dashboardName, LocalDate todaysDate, String traderUsername,
            List<BookActivity> bookActivites,
            Map<String, Metrics> summerisedMetrics, Map<String, Comparison> comparison) {
        this.dashBoardName = dashboardName.toUpperCase();
        this.todaysDate = todaysDate;
        this.traderUsername = traderUsername.toUpperCase();
        this.bookActivites = bookActivites;
        this.summerisedMetrics = summerisedMetrics;
        this.comparison = comparison;
    }

    // Flatten Version of books
    public static record BookActivity(
            String bookName,
            String costCenterName, String subDeskName,
            BigDecimal totalNotional, Integer version) {
    }

    // Flatten Version of metrics
    public static record Metrics(
            Long tradeCount,
            Double averageNotional,
            Double totalNotional) {
    }

    // Flatten Version of comparison
    public static record Comparison(
            Double difference,
            Double percentageChange) {
    }

}
