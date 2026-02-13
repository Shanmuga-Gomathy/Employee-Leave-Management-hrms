package com.example.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * LeaveType Entity
 *
 * Stores different types of leave available in the system.
 */
@Entity
@Table(name="leave_types")
@Getter
@Setter
public class LeaveType {
    // Primary key for leave type
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Type of leave (SICK, CASUAL, EARNED)
    @Enumerated(EnumType.STRING)
    @Column(unique = true,nullable = false)
    private LeaveTypeEnum name;

}
