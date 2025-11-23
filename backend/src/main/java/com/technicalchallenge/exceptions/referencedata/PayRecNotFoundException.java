package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a payrec is not found in the trading application.
 */

public class PayRecNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new PayRecNotFoundException when the PayRec is not found.
     */

    public PayRecNotFoundException(String fieldName, Object value) {
        super("PayRec is not found with " + fieldName + ": " + value);
    }

}
