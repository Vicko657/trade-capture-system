package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a user is not found in the trading application.
 */

public class UserNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new UserNotFoundException when the User is not found.
     */

    public UserNotFoundException(String fieldName, Object value) {
        super("User is not found with " + fieldName + ": " + value);
    }

}
