package com.technicalchallenge.exceptions;

public class CounterpartyNotFoundException extends RuntimeException {

    public CounterpartyNotFoundException(String message) {
        super(message);
    }

}
