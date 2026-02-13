package com.example.hrms.repository;

import com.example.hrms.entity.Employee;
import com.example.hrms.entity.LeaveBalance;
import com.example.hrms.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * LeaveBalanceRepository
 *
 * Handles database operations for LeaveBalance entity.
 * Used to manage employee leave balances.
 */
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    // Fetch leave balance for a specific employee and leave type
    Optional<LeaveBalance> findByEmployeeAndLeaveType(Employee employee, LeaveType leaveType);
}
