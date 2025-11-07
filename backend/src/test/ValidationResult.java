package com.technicalchallenge.validation;

import java.util.ArrayList;
import java.util.List;

import com.technicalchallenge.exceptions.ValidationException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ValidationResult {

    private final List<String> errors;

    // isValid - Validation is true and returns emptylist
    public static ValidationResult valid() {
        return new ValidationResult(new ArrayList<>());
    }

    // isNotValid - Validation is false and returns list of errors
    public static ValidationResult errors(List<String> errors) {
        return new ValidationResult(errors);
    }

    // isValid - Validation is true and returns emptylist
    public boolean isValid() {
        return errors.isEmpty();
    }

    // isNotValid - Validation is false and returns list of errors
    public boolean isNotValid() {
        return !errors.isEmpty();
    }

    // Getter - Errors
    public List<String> getErrors() {
        return errors;
    }

    public void throwifInvalid() {
        if (isNotValid()) {
            throw new ValidationException(errors);
        }
    }

}
