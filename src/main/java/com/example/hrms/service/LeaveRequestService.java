package com.example.hrms.service;

import com.example.hrms.dto.LeaveRequestDTO;
import com.example.hrms.entity.LeaveRequest;
import com.example.hrms.entity.LeaveStatus;
import com.example.hrms.entity.LeaveType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * LeaveRequestService
 *
 * Defines leave request operations:
 *  - Apply for leave
 *  - View leave history of an employee
 */
@Service
public interface LeaveRequestService {
    // Apply leave for an employee
    LeaveRequestDTO applyLeave(Long employeeId,
                               String leaveType,
                               LocalDate startDate,
                               LocalDate endDate,
                               String reason);

    // Get leave history by employee ID
    List<LeaveRequestDTO> getLeaveHistory(Long employeeId);
}
