package com.example.hrms.exception;

/**
 * ResourceNotFoundException
 *
 * Thrown when a requested resource is not found in the database.
 * Example: Employee not found, Leave request not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
