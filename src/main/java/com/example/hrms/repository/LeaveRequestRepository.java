package com.example.hrms.repository;

import com.example.hrms.entity.LeaveRequest;
import com.example.hrms.entity.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<LeaveRequest> findByEmployeeId(Long employeeId, Pageable pageable);
    // Fetch all leave requests by status (PENDING, APPROVED, REJECTED)
    Page<LeaveRequest> findByStatus(LeaveStatus status, Pageable pageable);
}
