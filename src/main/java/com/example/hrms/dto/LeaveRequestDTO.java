package com.example.hrms.dto;

import com.example.hrms.entity.LeaveStatus;
import com.example.hrms.entity.LeaveType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * LeaveRequestDTO
 *
 * Data Transfer Object used to transfer leave request data
 * between controller and service layers.
 *
 * This DTO does NOT expose entity objects directly.
 * It only contains primitive and safe values.
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