package com.technicalchallenge.validation;

import java.util.Collections;
import java.util.List;

import com.technicalchallenge.exceptions.ValidationException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ValidationResult {

    private final boolean valid;
    private final List<String> errors;

    // isValid - Error list is empty and returns emptylist
    public static ValidationResult isValid() {
        return new ValidationResult(true, Collections.emptyList());
    }

    // isNotValid - Error list has errors and returns list of errors
    public static ValidationResult isNotValid(List<String> errors) {
        return new ValidationResult(false, errors);
    }

    // Getter - Errors
    public List<String> getErrors() {
        return errors;
    }

    // throws a ValidationException when there is a validation error
    public void throwifNotValid() {
        if (!valid) {
            throw new ValidationException(errors);
        }
    }

}
