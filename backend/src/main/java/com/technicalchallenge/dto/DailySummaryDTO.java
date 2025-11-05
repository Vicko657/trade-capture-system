package com.technicalchallenge.dto;

import java.math.BigDecimal;
import java.util.List;

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

    // Summarised List of Book Activities
    private final List<BookActivity> bookActivites;

    public DailySummaryDTO(String dashboardName, String traderUsername,
            List<BookActivity> bookActivites, Long tradeExecuted,
            Double totalNotional, BigDecimal totalVolume, Double averagePrice, Double profitFactor) {
        this.dashBoardName = dashboardName.toUpperCase();
        this.traderUsername = traderUsername.toUpperCase();
        this.bookActivites = bookActivites;
    }

    // Flatten Version of books
    public static record BookActivity(
            String bookName,
            String costCenterName, String subDeskName, Long tradeCount,
            BigDecimal totalNotional, Integer version) {
    }

}
