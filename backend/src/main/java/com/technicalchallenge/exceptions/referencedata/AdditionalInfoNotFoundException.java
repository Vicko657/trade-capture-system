package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a additionalInfo is not found in the trading application.
 */

public class AdditionalInfoNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new AdditionalInfoNotFoundException when the AdditionalInfo is
     * not found.
     */

    public AdditionalInfoNotFoundException(String fieldName, Object value) {
        super("AdditionalInfo is not found with " + fieldName + ": " + value);
    }

}
