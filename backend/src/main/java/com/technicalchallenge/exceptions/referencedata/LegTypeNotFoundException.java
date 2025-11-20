package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a legType is not found in the trading application.
 */

public class LegTypeNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new LegTypeNotFoundException when the LegType is not found.
     */

    public LegTypeNotFoundException(String fieldName, Object value) {
        super("LegType is not found with " + fieldName + ": " + value);
    }

}
