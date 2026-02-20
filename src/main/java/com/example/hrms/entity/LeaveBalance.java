package com.example.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * LeaveBalance Entity
 *
 * Represents the remaining leave days of an employee
 * for a specific leave type.
 *
 * Example:
 * One employee can have multiple leave balances:
 *  - Sick Leave → 10 days
 *  - Casual Leave → 5 days
 *
 * This entity helps in:
 *  - Tracking available leave days
 *  - Validating leave requests
 *  - Deducting leave after approval
 */
@Entity
@Table(name = "leave_balances")
@Getter
@Setter
public class LeaveBalance {

    // Primary key of the leave balance
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many leave balances belong to one employee
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Many leave balances can belong to one leave type
    @ManyToOne
    private LeaveType leaveType;

    // Number of leave days remaining for the employee
    private int remainingDays;

}
