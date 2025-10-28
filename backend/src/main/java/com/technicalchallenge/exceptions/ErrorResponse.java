package com.technicalchallenge.exceptions;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
// Contructs Error Response
public class ErrorResponse {
    // HTTP Status Code
    private final Integer statusCode;
    // Error Message
    private final List<String> messages;
    // Time of error
    private final LocalDateTime timeStamp;

}
