package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a privilege is not found in the trading application.
 */

public class PrivilegeNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new PrivilegeNotFoundException when the Privilege is not found.
     */

    public PrivilegeNotFoundException(String fieldName, Object value) {
        super("Privilege is not found with " + fieldName + ": " + value);
    }

}
