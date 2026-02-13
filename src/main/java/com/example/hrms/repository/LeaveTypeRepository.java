package com.example.hrms.repository;

import com.example.hrms.entity.LeaveType;
import com.example.hrms.entity.LeaveTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * LeaveTypeRepository
 *
 * Handles database operations for LeaveType entity.
 * Used to fetch leave type details based on enum name.
 */
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    // Find LeaveType by enum name (SICK, CASUAL, EARNED)
    Optional<LeaveType> findByName(LeaveTypeEnum name);
}
