package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a subdesk is not found in the trading application.
 */

public class SubDeskNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new SubDeskNotFoundException when the SubDesk is not found.
     */

    public SubDeskNotFoundException(String fieldName, Object value) {
        super("SubDesk is not found with " + fieldName + ": " + value);
    }

}
