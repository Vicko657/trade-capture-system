package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a cashflow is not found in the trading application.
 */

public class CashflowNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new CashflowNotFoundException when the Cashflow is not
     * found.
     */

    public CashflowNotFoundException(String fieldName, Object value) {
        super("Cashflow is not found with " + fieldName + ": " + value);
    }

}
