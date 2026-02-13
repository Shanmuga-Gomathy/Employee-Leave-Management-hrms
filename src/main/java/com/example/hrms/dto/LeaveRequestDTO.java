package com.example.hrms.dto;

import com.example.hrms.entity.LeaveStatus;
import com.example.hrms.entity.LeaveType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/*
 This DTO is used to send and receive leave request data.

 It contains:
 - Leave request ID
 - Employee ID
 - Leave type
 - Start date
 - End date
 - Total number of leave days
 - Leave status (PENDING, APPROVED, REJECTED)
 - Reason for leave

 This DTO is used in:
 - Applying for leave
 - Viewing leave history
 - Manager approval or rejection
*/
@Getter
@Setter
public class LeaveRequestDTO {
    private Long id;
    private Long employeeId;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalDays;
    private LeaveStatus status;
    private String reason;
}
