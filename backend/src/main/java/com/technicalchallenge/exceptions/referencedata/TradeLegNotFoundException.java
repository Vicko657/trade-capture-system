package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a tradeleg is not found in the trading application.
 */

public class TradeLegNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new TradeLegNotFoundException when the TradeLeg is not found.
     */

    public TradeLegNotFoundException(String fieldName, Object value) {
        super("TradeLeg is not found with " + fieldName + ": " + value);
    }

}
