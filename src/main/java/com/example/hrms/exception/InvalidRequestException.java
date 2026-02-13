package com.example.hrms.exception;

/**
 * InvalidRequestException
 *
 * Thrown when a business rule validation fails.
 * Example: insufficient leave balance, invalid date range.
 */
public class InvalidRequestException extends RuntimeException{
    public InvalidRequestException(String message){

    }
}
