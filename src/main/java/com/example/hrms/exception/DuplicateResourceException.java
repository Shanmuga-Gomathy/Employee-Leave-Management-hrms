package com.example.hrms.exception;

/**
 * DuplicateResourceException
 *
 * Thrown when trying to create a resource
 * that already exists in the system.
 *
 * Example:
 *  - Creating an employee with an email
 *    that is already registered.
 *
 * This is a custom runtime exception
 * used for business validation errors.
 */
public class DuplicateResourceException extends RuntimeException {

    // Constructor that accepts custom error message
    public DuplicateResourceException(String message) {
        super(message);
    }
}