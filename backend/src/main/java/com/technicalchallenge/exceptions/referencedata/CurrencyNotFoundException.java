package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a currency is not found in the trading application.
 */

public class CurrencyNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new CurrencyNotFoundException when the Currency is not found.
     */

    public CurrencyNotFoundException(String fieldName, Object value) {
        super("Currency is not found with " + fieldName + ": " + value);
    }

}
