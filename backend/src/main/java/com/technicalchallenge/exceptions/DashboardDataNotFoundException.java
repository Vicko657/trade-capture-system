package com.technicalchallenge.exceptions;

import lombok.Getter;

/**
 * Thrown when data is not found for the authenticated user to view their
 * dashboards
 */
@Getter
public class DashboardDataNotFoundException extends RuntimeException {
    /**
     * Constructs a new DashboardDataNotFoundException when the dashboard data is
     * not found and has no content.
     *
     * @param username data is not found
     */
    public DashboardDataNotFoundException(String message) {
        super(message);
    }

}
