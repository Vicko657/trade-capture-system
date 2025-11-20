package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a index is not found in the trading application.
 */

public class IndexNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new IndexNotFoundException when the Index is not found.
     */

    public IndexNotFoundException(String fieldName, Object value) {
        super("Index is not found with " + fieldName + ": " + value);
    }

}
