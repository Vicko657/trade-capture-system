package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a trade is not found in the trading application.
 */

public class TradeStatusNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new TradeNotFoundException when the Trade is not found.
     */

    public TradeStatusNotFoundException(String fieldName, Object value) {
        super("TradeStatus is not found with " + fieldName + ": " + value);
    }

    //
    public TradeStatusNotFoundException(String fieldName) {
        super("TradeStatus is not found with " + fieldName + "status");
    }

}
