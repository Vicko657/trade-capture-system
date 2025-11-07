package com.technicalchallenge.exceptions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The Global Exception Handler
 * 
 * <p>
 * Handles errors and exceptions that may occur in the system
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

        /**
         * MethodArguementNotValidException:
         * 
         * Handles @Valid @Validated errors
         */
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

        /**
         * ValidationException:
         * 
         * Handles business validation errors
         */
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ErrorResponse> handleBusinessValidationErrors(
                        ValidationException e, HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                                "Validation Exception", e.getErrors(), null,
                                LocalDateTime.now(), request.getRequestURI());

                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * EntityNotFoundException:
         * 
         * Handles entities are not found
         */
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

        /**
         * InActiveException:
         * 
         * Handles entities are not active
         */
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(InActiveException.class)
        public ResponseEntity<ErrorResponse> handleInActiveEntity(
                        InActiveException e, HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                                "InActive Exception", e.getErrors(), null,
                                LocalDateTime.now(), request.getRequestURI());

                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * InvalidSearchException:
         * 
         * Handles invalid search requests with multi criteria search, filter and rsql
         */
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

        /**
         * AccessDeniedException:
         * 
         * Handles denied privilege access for Spring Security
         */
        @ResponseStatus(HttpStatus.UNAUTHORIZED)
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleUnauthorizedAccess(
                        AccessDeniedException e, HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized",
                                "Unauthorized Exception", null, e
                                                .getMessage(),
                                LocalDateTime.now(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        /**
         * HTTPClientErrorException:
         * 
         * Handles forbidden access for Spring Security
         */
        @ResponseStatus(HttpStatus.FORBIDDEN)
        @ExceptionHandler(HttpClientErrorException.Forbidden.class)
        public ResponseEntity<ErrorResponse> handleForbiddenAccess(
                        HttpClientErrorException.Forbidden e, HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden",
                                "Forbidden Exception", null, e
                                                .getMessage(),
                                LocalDateTime.now(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        /**
         * DashboardDataNotFoundException:
         * 
         * Handles data not found for trader's dashboard
         */
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

        /**
         * InternalServerErrorException:
         * 
         * Handles internal service errors
         */
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        @ExceptionHandler(InternalServerError.class)
        public ResponseEntity<ErrorResponse> handleInternalServerError(
                        InternalServerError e, HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Internal Server Error",
                                "Internal Server Error Exception", null, e
                                                .getMessage(),
                                LocalDateTime.now(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

}
