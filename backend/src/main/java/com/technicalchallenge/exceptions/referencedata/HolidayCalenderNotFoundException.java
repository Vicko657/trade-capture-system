package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a holidaycalendar is not found in the trading application.
 */

public class HolidayCalenderNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new HolidayCalenderNotFoundException when the HolidayCalendar is
     * not
     * found.
     */

    public HolidayCalenderNotFoundException(String fieldName, Object value) {
        super("HolidayCalende is not found with " + fieldName + ": " + value);
    }

}
