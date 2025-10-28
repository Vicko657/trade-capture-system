package com.technicalchallenge.exceptions;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

    private List<String> errors;

    public ValidationException(List<String> errors) {
        super(String.join(",", errors));
        this.errors = new ArrayList<>(errors);
    }

    public ValidationException(String errors) {
        super(errors);
    }
}
