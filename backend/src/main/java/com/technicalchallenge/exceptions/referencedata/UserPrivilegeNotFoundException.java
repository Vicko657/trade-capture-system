package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when the Userprivilege is not found in the trading application.
 */

public class UserPrivilegeNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new UserPrivilegeNotFoundException when the UserPrivilege is not
     * found.
     */

    public UserPrivilegeNotFoundException(String fieldName, Object value) {
        super("UserPrivilege is not found with " + fieldName + ": " + value);
    }

}
