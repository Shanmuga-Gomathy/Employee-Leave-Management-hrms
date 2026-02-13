package com.example.hrms.service.impl;

import com.example.hrms.dto.LeaveRequestDTO;
import com.example.hrms.entity.*;
import com.example.hrms.repository.EmployeesRepository;
import com.example.hrms.repository.LeaveBalanceRepository;
import com.example.hrms.repository.LeaveRequestRepository;
import com.example.hrms.repository.LeaveTypeRepository;
import com.example.hrms.service.LeaveRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LeaveRequestServiceImpl
 *
 * Handles leave request business logic.
 * - Apply leave
 * - Validate leave rules
 * - Check leave balance
 * - Fetch leave history
 * - Convert Entity to DTO
 */
@Service
@Slf4j
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeesRepository employeesRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveRequestServiceImpl(
            LeaveRequestRepository leaveRequestRepository,
            EmployeesRepository employeesRepository,
            LeaveBalanceRepository leaveBalanceRepository,
            LeaveTypeRepository leaveTypeRepository) {

        this.leaveRequestRepository = leaveRequestRepository;
        this.employeesRepository = employeesRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    // Apply leave for an employee
    @Override
    public LeaveRequestDTO applyLeave(Long employeeId,
                                      String leaveTypeName,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      String reason) {

        log.info("Applying leave for employeeId: {}, leaveType: {}", employeeId, leaveTypeName);

        if (endDate.isBefore(startDate)) {
            log.warn("Invalid date range: {} to {}", startDate, endDate);
            throw new RuntimeException("Invalid date range");
        }

        Employee employee = employeesRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", employeeId);
                    return new RuntimeException("Employee not found");
                });

        LeaveType leaveType = leaveTypeRepository
                .findByName(LeaveTypeEnum.valueOf(leaveTypeName))
                .orElseThrow(() -> {
                    log.error("Leave type not found: {}", leaveTypeName);
                    return new RuntimeException("Leave type not found");
                });

        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeAndLeaveType(employee, leaveType)
                .orElseThrow(() -> {
                    log.error("Leave balance not found for employeeId: {} and leaveType: {}",
                            employeeId, leaveTypeName);
                    return new RuntimeException("Leave balance not found");
                });

        int days = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        log.info("Requested leave days: {}, Remaining balance: {}",
                days, balance.getRemainingDays());

        if (balance.getRemainingDays() < days) {
            log.warn("Insufficient leave balance for employeeId: {}", employeeId);
            throw new RuntimeException("Insufficient leave balance");
        }

        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setLeaveType(leaveType);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setTotalDays(days);
        request.setStatus(LeaveStatus.PENDING);
        request.setReason(reason);

        LeaveRequest saved = leaveRequestRepository.save(request);

        log.info("Leave request created successfully with ID: {}", saved.getId());

        return convertToDTO(saved);
    }

    // Fetch leave history for employee
    @Override
    public List<LeaveRequestDTO> getLeaveHistory(Long employeeId) {

        log.info("Fetching leave history for employeeId: {}", employeeId);

        List<LeaveRequestDTO> history = leaveRequestRepository
                .findByEmployeeId(employeeId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        log.info("Total leave records found: {}", history.size());

        return history;
    }

    // Convert LeaveRequest entity to DTO
    private LeaveRequestDTO convertToDTO(LeaveRequest request) {

        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setId(request.getId());
        dto.setEmployeeId(request.getEmployee().getId());
        dto.setLeaveType(request.getLeaveType());
        dto.setStartDate(request.getStartDate());
        dto.setEndDate(request.getEndDate());
        dto.setTotalDays(request.getTotalDays());
        dto.setStatus(request.getStatus());
        dto.setReason(request.getReason());

        return dto;
    }
}