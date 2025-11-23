package com.technicalchallenge.validation;

import java.util.ArrayList;
import java.util.List;

import com.technicalchallenge.exceptions.ValidationException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ValidationResult {

    private final List<String> errors;

    // isValid - Validation is true and returns emptylist
    public static ValidationResult isValid() {
        return new ValidationResult(new ArrayList<>());
    }

    // isNotValid - Validation is false and returns list of errors
    public static ValidationResult isNotValid(List<String> errors) {
        return new ValidationResult(errors);
    }

    // isValid - Validation is true and returns emptylist
    public boolean valid() {
        return errors.isEmpty();
    }

    // isNotValid - Validation is false and returns list of errors
    public boolean invalid() {
        return !errors.isEmpty();
    }

    // Getter - Errors
    public List<String> getErrors() {
        return errors;
    }

    public void throwifNotValid() {
        if (invalid()) {
            throw new ValidationException(errors);
        }
    }

}
