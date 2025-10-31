package com.technicalchallenge.exceptions;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class InActiveException extends RuntimeException {

    private List<String> errors;

    public InActiveException(List<String> errors) {
        super(String.join(",", errors));
        this.errors = new ArrayList<>(errors);
    }

    public InActiveException(String errors) {
        super(errors);
    }

}
