package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a trade is not found in the trading application.
 */

public class TradeNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new TradeNotFoundException when the Trade is not found.
     */

    public TradeNotFoundException(String fieldName, Object value) {
        super("Trade is not found with " + fieldName + ": " + value);
    }

}
