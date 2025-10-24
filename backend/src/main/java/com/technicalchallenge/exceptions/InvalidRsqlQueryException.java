package com.technicalchallenge.exceptions;

public class InvalidRsqlQueryException extends RuntimeException {
    public InvalidRsqlQueryException(String message) {
        super(message);
    }
}
