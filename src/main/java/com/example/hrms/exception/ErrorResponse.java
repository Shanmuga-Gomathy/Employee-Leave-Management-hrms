package com.example.hrms.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ErrorResponse
 *
 * Standard error response format used for all API exceptions.
 */
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;// HTTP status code
    private List<String> errors;

    // Constructor to initialize error response
    public ErrorResponse(LocalDateTime timestamp, int status, List<String> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.errors = errors;
    }

    // Returns error timestamp
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Returns HTTP status code
    public int getStatus() {
        return status;
    }

    // Returns list of error messages
    public List<String> getErrors() {
        return errors;
    }
}