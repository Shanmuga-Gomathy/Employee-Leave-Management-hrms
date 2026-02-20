package com.example.hrms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * GlobalExceptionHandler
 *
 * Centralized exception handling for the entire application.
 * Provides consistent JSON error responses for all API errors.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 400 - DTO validation errors (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<String> errors = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }

        return buildResponse(HttpStatus.BAD_REQUEST, errors);
    }

    /**
     * 400 - Invalid path variable type
     * Example: /employees/abc (where id should be Long)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                List.of("Invalid path variable type")
        );
    }

    /**
     * 400 - Invalid or missing request body
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                List.of("Request body is missing or invalid")
        );
    }

    /**
     * 404 - Resource not found (custom)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                List.of(ex.getMessage())
        );
    }

    /**
     * 404 - Endpoint not found
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                List.of("Endpoint not found")
        );
    }

    /**
     * 405 - Unsupported HTTP method
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex) {

        return buildResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                List.of("HTTP method not supported")
        );
    }

    /**
     * 400 - Business rule validation error
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(
            InvalidRequestException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                List.of(ex.getMessage())
        );
    }

    /**
     * 409 - Duplicate resource
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex) {

        return buildResponse(
                HttpStatus.CONFLICT,
                List.of(ex.getMessage())
        );
    }

    /**
     * 403 - Access denied
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex) {

        return buildResponse(
                HttpStatus.FORBIDDEN,
                List.of("Access Denied")
        );
    }

    /**
     * 401 - Authentication failure
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex) {

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                List.of("Invalid username or password")
        );
    }

    /**
     * 400 - Illegal argument
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                List.of(ex.getMessage())
        );
    }

    /**
     * 500 - Any unexpected exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                List.of("Internal Server Error")
        );
    }

    /**
     * Helper method to build consistent error response
     */
    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            List<String> errors) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                errors
        );

        return new ResponseEntity<>(response, status);
    }
}