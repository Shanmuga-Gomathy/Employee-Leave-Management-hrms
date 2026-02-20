package com.example.hrms.service;

import com.example.hrms.dto.LeaveRequestDTO;
import org.springframework.data.domain.Page;

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
    Page<LeaveRequestDTO> getPendingRequests(int page, int size);

    // Approve leave request by request ID
    LeaveRequestDTO approveLeave(Long requestId);

    // Reject leave request by request ID
    LeaveRequestDTO rejectLeave(Long requestId);
}
