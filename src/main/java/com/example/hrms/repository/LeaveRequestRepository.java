package com.example.hrms.repository;

import com.example.hrms.entity.LeaveRequest;
import com.example.hrms.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * LeaveRequestRepository
 *
 * Handles database operations for LeaveRequest entity.
 * Used for fetching leave requests based on employee or status.
 */
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    // Fetch all leave requests for a specific employee
    List<LeaveRequest> findByEmployeeId(Long employeeId);
    // Fetch all leave requests by status (PENDING, APPROVED, REJECTED)
    List<LeaveRequest> findByStatus(LeaveStatus status);
}
