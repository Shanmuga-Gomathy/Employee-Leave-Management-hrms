package com.example.hrms.service;

import com.example.hrms.dto.LeaveRequestDTO;

import java.util.List;

/**
 * ManagerService
 *
 * Defines manager operations related to leave management.
 *
 * Responsibilities:
 *  - View all pending leave requests
 *  - Approve a leave request
 *  - Reject a leave request
 *
 * These methods are intended to be accessed only by users
 * with MANAGER role.
 */
public interface ManagerService {
    // Get all leave requests with PENDING status
    List<LeaveRequestDTO> getPendingRequests();

    // Approve leave request by request ID
    LeaveRequestDTO approveLeave(Long requestId);

    // Reject leave request by request ID
    LeaveRequestDTO rejectLeave(Long requestId);
}
