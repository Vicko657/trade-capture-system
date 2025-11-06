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
        public ResponseEntity<ErrorResponse> handleFieldValidationErrors(
                        MethodArgumentNotValidException e, HttpServletRequest request) {

                List<String> validationResult = new ArrayList<>();

                e.getBindingResult().getAllErrors().forEach(error -> {
                        String message = error.getDefaultMessage();
                        validationResult.add(message);
                });

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                                "Field Error Exception",
                                validationResult, null,
                                LocalDateTime.now(), request.getRequestURI());

                return ResponseEntity.badRequest().body(errorResponse);
        }

        // Handles Business Validation Errors
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ErrorResponse> handleBusinessValidationErrors(
                        ValidationException e, HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                                "Validation Exception", e.getErrors(), null,
                                LocalDateTime.now(), request.getRequestURI());

                return ResponseEntity.badRequest().body(errorResponse);
        }

        // Handles Entities not found
        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleEntityNotFound(
                        EntityNotFoundException e, HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found",
                                "Entity Not Found Exception",
                                null, e
                                                .getMessage(),
                                LocalDateTime.now(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Handles Entities not active
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(InActiveException.class)
        public ResponseEntity<ErrorResponse> handleInActiveEntity(
                        InActiveException e, HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                                "InActive Exception", e.getErrors(), null,
                                LocalDateTime.now(), request.getRequestURI());

                return ResponseEntity.badRequest().body(errorResponse);
        }

        // Handles Invalid Search requests
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(InvalidSearchException.class)
        public ResponseEntity<ErrorResponse> handleInValidSearch(
                        InvalidSearchException e, HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                                "Invalid Search Exception", null, e
                                                .getMessage(),
                                LocalDateTime.now(), request.getRequestURI());

                return ResponseEntity.badRequest().body(errorResponse);
        }

        // Handles Denied Privilege Access
        @ResponseStatus(HttpStatus.FORBIDDEN)
        @ExceptionHandler(UnauthorizedAccessException.class)
        public ResponseEntity<ErrorResponse> handleUnauthorizedAccess(
                        UnauthorizedAccessException e, HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden",
                                "Denied Access Exception", null, e
                                                .getMessage(),
                                LocalDateTime.now(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        // Handles Data not found for trader's dashboard
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @ExceptionHandler(DashboardDataNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleDashboardDataNotFound(
                        DashboardDataNotFoundException e, HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NO_CONTENT.value(), "No Content",
                                "Dashboard Data Not Found Exception", null, e
                                                .getMessage(),
                                LocalDateTime.now(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(errorResponse);
        }

}
