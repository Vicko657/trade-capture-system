package com.technicalchallenge.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.technicalchallenge.dto.DailySummaryDTO;
import com.technicalchallenge.dto.TradeSummaryDTO;
import com.technicalchallenge.security.ApplicationUserDetails;
import com.technicalchallenge.service.DashboardViewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Rest Controller for viewing personal dashboards and blotter system
 * 
 * API endpoints to complete READ operations.
 */
@RestController
@RequestMapping("/api/trades/dashboard")
@Tag(name = "Dashboard Views", description = "Trader dashboard and blotter system including personal trades, trades summary, daily summary and book level activity")
public class DashboardViewController {
        private static final Logger logger = LoggerFactory.getLogger(DashboardViewController.class);

        @Autowired
        private DashboardViewService dashboardViewService;

        @Operation(summary = "Get the trader's personal trades view", description = "Retrieves all the user's trades.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved all the user's trades", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TradeSummaryDTO.class))),
                        @ApiResponse(responseCode = "401", description = "User's access denied"),
                        @ApiResponse(responseCode = "204", description = "No Data for Dashboard is found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/my-trades")
        @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
        @ResponseBody
        public ResponseEntity<TradeSummaryDTO> getTraderDashboard(
                        @AuthenticationPrincipal ApplicationUserDetails userDetails,
                        Pageable pageable) {
                String username = userDetails.getUsername();
                logger.info("Fetching the user's personal trades: {}", username);

                TradeSummaryDTO personalDashboard = dashboardViewService.getTraderDashboard(username, pageable);

                if (personalDashboard == null || personalDashboard.getTrades() == null
                                || personalDashboard.getTrades().isEmpty()) {
                        return ResponseEntity.noContent().build();
                }

                return ResponseEntity.ok(personalDashboard);

        }

        @Operation(summary = "Get the trader's portfolio summaries view", description = "Retrieves the trader's portfolio summary including the total notional amounts by currency, total number of trades by status, breakdowns by trade type and counterparties and risk exposure.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved all the user's trade summary", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TradeSummaryDTO.class))),
                        @ApiResponse(responseCode = "401", description = "User's access denied"),
                        @ApiResponse(responseCode = "204", description = "No Data for Dashboard is found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/summary")
        @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
        @ResponseBody
        public ResponseEntity<TradeSummaryDTO> getPortfolioSummaries(
                        @AuthenticationPrincipal ApplicationUserDetails userDetails) {
                String username = userDetails.getUsername();
                logger.info("Fetching the user's trade summary: {}", username);

                TradeSummaryDTO portfolioDashboard = dashboardViewService.getTradePortfolioSummaries(username);

                if (portfolioDashboard == null || portfolioDashboard.getNotionalByTradeType() == null
                                || portfolioDashboard.getNotionalByCounterparty() == null
                                || portfolioDashboard.getRiskExposure() == null
                                || portfolioDashboard.getTotalCountByStatus() == null
                                || portfolioDashboard.getTotalNotionalByCurrency() == null) {
                        return ResponseEntity.noContent().build();
                }

                return ResponseEntity.ok(portfolioDashboard);

        }

        @Operation(summary = "Get the trader's book level activities view", description = "Retrieves the trader's book level activities view including book-level trade aggregation.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved all the user's book level activity", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DailySummaryDTO.class))),
                        @ApiResponse(responseCode = "401", description = "User's access denied"),
                        @ApiResponse(responseCode = "204", description = "No Data for Dashboard is found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/book/{id}/trades")
        @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
        @ResponseBody
        public ResponseEntity<DailySummaryDTO> getBookActivites(
                        @AuthenticationPrincipal ApplicationUserDetails userDetails,
                        @PathVariable("id") Long id) {

                String username = userDetails.getUsername();
                logger.info("Fetching the user's book level activity: {}", username);

                DailySummaryDTO bookActivitiesDashboard = dashboardViewService.getBookLevelActivity(username, id);

                if (bookActivitiesDashboard == null || bookActivitiesDashboard.getBookActivites() == null
                                || bookActivitiesDashboard.getBookActivites().isEmpty()) {
                        return ResponseEntity.noContent().build();
                }

                return ResponseEntity.ok(bookActivitiesDashboard);

        }

        @Operation(summary = "Get the trader's daily trading statistics view", description = "Retrieves the trader's daily summary including the daily summaried statistics and comparison to previous trading days.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved all the user's daily trading statistics", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DailySummaryDTO.class))),
                        @ApiResponse(responseCode = "401", description = "User's access denied"),
                        @ApiResponse(responseCode = "204", description = "No Data for Dashboard is found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/daily-summary")
        @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
        @ResponseBody
        public ResponseEntity<DailySummaryDTO> getDailyTradingStatistics(
                        @AuthenticationPrincipal ApplicationUserDetails userDetails) {

                String username = userDetails.getUsername();
                logger.info("Fetching the user's daily trading statistics: {}", username);

                DailySummaryDTO dailyTradingSummary = dashboardViewService.getDailyTradingStatistics(username);

                if (dailyTradingSummary == null || dailyTradingSummary.getComparison() == null
                                || dailyTradingSummary.getSummarisedMetrics() == null) {
                        return ResponseEntity.noContent().build();
                }

                return ResponseEntity.ok(dailyTradingSummary);

        }

}
