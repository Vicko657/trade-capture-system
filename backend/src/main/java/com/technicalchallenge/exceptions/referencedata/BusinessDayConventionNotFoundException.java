package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a businessdayconvention is not found in the trading application.
 */

public class BusinessDayConventionNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new BusinessDayConventionNotFoundException when the
     * BusinessDayConvention is
     * not
     * found.
     */

    public BusinessDayConventionNotFoundException(String fieldName, Object value) {
        super("BusinessDayConvention is not found with " + fieldName + ": " + value);
    }

}
