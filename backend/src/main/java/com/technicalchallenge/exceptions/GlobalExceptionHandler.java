package com.technicalchallenge.exceptions;

import java.time.LocalDateTime;
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
public class GlobalExceptionHandler {

    // Handles @Valid @Validated Errors
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

    // Handles Business Validation Errors
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessValidationErrors(
            ValidationException e) {

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getErrors(),
                LocalDateTime.now());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    // Handles Book not found
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> handleBookNotFound(
            BookNotFoundException e) {

        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // Handles Counterparty not found
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CounterpartyNotFoundException.class)
    public ResponseEntity<String> handleCounterpartyNotFound(
            CounterpartyNotFoundException e) {

        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // Handles TradeStatus not found
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TradeStatusNotFoundException.class)
    public ResponseEntity<String> handleTradeStatusNotFound(
            TradeStatusNotFoundException e) {

        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // Handles TradeType not found
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TradeTypeNotFoundException.class)
    public ResponseEntity<String> handleTradeTypeNotFound(
            TradeTypeNotFoundException e) {

        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // Handles TradeSubType not found
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TradeSubTypeNotFoundException.class)
    public ResponseEntity<String> handleTradeSubTypeNotFound(
            TradeSubTypeNotFoundException e) {

        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
