package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a desk is not found in the trading application.
 */

public class DeskNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new DeskNotFoundException when the Desk is not found.
     */

    public DeskNotFoundException(String fieldName, Object value) {
        super("Desk is not found with " + fieldName + ": " + value);
    }

}
