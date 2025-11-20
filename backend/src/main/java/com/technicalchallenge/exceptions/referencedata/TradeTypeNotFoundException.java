package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when the TradeType is not found in the trading application.
 */

public class TradeTypeNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new TradeTypeNotFoundException when the TradeType is not
     * found.
     */

    public TradeTypeNotFoundException(String fieldName, Object value) {
        super("TradeType is not found with " + fieldName + ": " + value);
    }

}
