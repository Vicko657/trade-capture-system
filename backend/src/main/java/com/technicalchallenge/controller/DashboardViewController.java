package com.technicalchallenge.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.technicalchallenge.security.ApplicationUserDetails;
import com.technicalchallenge.service.DashboardViewService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/trades/dashboard")
public class DashboardViewController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardViewController.class);

    @Autowired
    private DashboardViewService dashboardViewService;

    @GetMapping("/my-trades")
    @Operation(summary = "Trader's personal trades", description = "Cancels an existing trade by changing its status to cancelled")
    public ResponseEntity<?> getTraderDashboard(@AuthenticationPrincipal ApplicationUserDetails userDetails,
            Pageable pageable) {

        String username = userDetails.getUsername();

        return ResponseEntity.ok(dashboardViewService.getTraderDashboard(username, pageable));

    }

}
