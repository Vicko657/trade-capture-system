package com.technicalchallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleFieldValidationExceptions(
            MethodArgumentNotValidException e) {

        // Targets tradeDate validation error
        FieldError tradeDateError = e.getBindingResult().getFieldError("tradeDate");
        if (tradeDateError != null) {
            return tradeDateError.getDefaultMessage();
        }
        // Targets notional validation error
        FieldError notionalError = e.getBindingResult().getFieldError("notional");
        if (notionalError != null) {
            return notionalError.getDefaultMessage();
        }

        return "Validation has failed";
    }

}
