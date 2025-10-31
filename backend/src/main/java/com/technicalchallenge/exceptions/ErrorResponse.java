package com.technicalchallenge.exceptions;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
// Contructs Error Response
public class ErrorResponse {
    // HTTP Status Code
    private final Integer statusCode;
    // Type of Error
    private final String error;
    // Type of Error
    private final String exception;
    // List of error Messages
    private List<String> messages;
    // Error Message
    private String message;
    // Time of error
    private final LocalDateTime timeStamp;
    // Path
    private final String path;
}
