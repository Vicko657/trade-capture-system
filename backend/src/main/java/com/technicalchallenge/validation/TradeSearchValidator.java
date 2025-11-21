package com.technicalchallenge.validation;

import org.springframework.stereotype.Component;

import com.technicalchallenge.dto.SearchTradeByCriteria;
import com.technicalchallenge.exceptions.InvalidSearchException;

/**
 * Search Validator
 * 
 * <p>
 * Validates the business rules for trade searches.
 * 
 * </p>
 * 
 */
@Component
public class TradeSearchValidator {

    /**
     * 
     * 1. Validation for SearchTradeByCriteria & Filtering
     * 
     */
    public void validateSearch(SearchTradeByCriteria searchTradeByCriteria) {
        // Validate date range for tradeDate
        if (searchTradeByCriteria.tradeStartDate() != null && searchTradeByCriteria.tradeEndDate() != null
                && searchTradeByCriteria.tradeEndDate().isBefore(searchTradeByCriteria.tradeStartDate())) {
            throw new InvalidSearchException("End date cannot be before start date");
        }
    }

    /**
     * 
     * 2. Validation for RSQLSearch
     * 
     */
    public void validateRSQLSearch(String query) {
        // Validate query - if the query is null or missing the exception is thrown
        if (query == null || query.isEmpty()) {
            throw new InvalidSearchException("Query must not be null or empty");
        }

    }

}
