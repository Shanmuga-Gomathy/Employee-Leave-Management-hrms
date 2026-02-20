package com.example.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * LeaveRequest Entity
 *
 * Represents a leave request submitted by an employee.
 * Stores leave details, duration, reason and approval status.
 */
@Entity
@Getter
@Setter
public class LeaveRequest {

    // Primary key of the leave request
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //Employee who applied for leave
    @ManyToOne
    private Employee employee;

    //Type of leave (Casual, Sick, Earned)
    @ManyToOne
    private LeaveType leaveType;

    private LocalDate startDate;
    private LocalDate endDate;

    private int totalDays;
    //Current status of leave (PENDING, APPROVED, REJECTED)
    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    private String reason;

}
