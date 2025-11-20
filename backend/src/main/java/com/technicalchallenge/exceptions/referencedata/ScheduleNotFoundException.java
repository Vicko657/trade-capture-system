package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a schedule is not found in the trading application.
 */

public class ScheduleNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new ScheduleNotFoundException when the Schedule is not found.
     */

    public ScheduleNotFoundException(String fieldName, Object value) {
        super("Schduele is not found with " + fieldName + ": " + value);
    }

}
