package com.example.hrms.service;

import org.springframework.stereotype.Service;

/**
 * LeaveTypeService
 *
 * Responsible for initializing default leave types
 * when the application starts.
 */
@Service
public interface LeaveTypeService {

    // Initialize default leave types in the database
    void initializeLeaveTypes();
}