package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a ccostCenter is not found in the trading application.
 */

public class CostCenterNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new CostCenterNotFoundException when the CostCenter is not
     * found.
     */

    public CostCenterNotFoundException(String fieldName, Object value) {
        super("CostCenter is not found with " + fieldName + ": " + value);
    }

}
