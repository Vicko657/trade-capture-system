package com.technicalchallenge.exceptions.referencedata;

import com.technicalchallenge.exceptions.EntityNotFoundException;

/**
 * Thrown when a book is not found in the trading application.
 */

public class BookNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new BookNotFoundException when the Book is not found.
     */

    public BookNotFoundException(String fieldName, Object value) {
        super("Book is not found with " + fieldName + ": " + value);
    }

}
