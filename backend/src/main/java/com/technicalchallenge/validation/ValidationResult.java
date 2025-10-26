package com.technicalchallenge.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ValidationResult {

    // Handles All Validation Errors
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleFieldValidationErrors(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        List<String> validationResult = new ArrayList<>();

        e.getBindingResult().getAllErrors().forEach(error -> {
            String message = error.getDefaultMessage();
            validationResult.add(message);
        });

        return ResponseEntity.badRequest().body(validationResult);
    }

}
