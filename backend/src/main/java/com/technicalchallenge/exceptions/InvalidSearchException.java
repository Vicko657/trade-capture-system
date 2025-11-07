package com.technicalchallenge.exceptions;

import lombok.Getter;

@Getter
public class InvalidSearchException extends RuntimeException {

    public InvalidSearchException(String message) {
        super(message);
    }
}
