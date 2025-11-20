package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a user's profile is not found in the trading application.
 */

public class UserProfileNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new UserProfileNotFoundException when the UserProfile is not
     * found.
     */

    public UserProfileNotFoundException(String fieldName, Object value) {
        super("UserProfife is not found with " + fieldName + ": " + value);
    }

}
