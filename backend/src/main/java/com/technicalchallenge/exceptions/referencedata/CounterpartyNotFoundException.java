package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a counterparty is not found in the trading application.
 */

public class CounterpartyNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new CounterpartyNotFoundException when the Counterparty is not
     * found.
     */

    public CounterpartyNotFoundException(String fieldName, Object value) {
        super("Counterparty is not found with " + fieldName + ": " + value);
    }

}
