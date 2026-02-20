package com.example.hrms.service.impl;

import com.example.hrms.dto.LeaveRequestDTO;
import com.example.hrms.entity.*;
import com.example.hrms.exception.InvalidRequestException;
import com.example.hrms.exception.ResourceNotFoundException;
import com.example.hrms.mapper.LeaveRequestMapper;
import com.example.hrms.repository.EmployeesRepository;
import com.example.hrms.repository.LeaveBalanceRepository;
import com.example.hrms.repository.LeaveRequestRepository;
import com.example.hrms.repository.LeaveTypeRepository;
import com.example.hrms.service.LeaveRequestService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * LeaveRequestServiceImpl
 *
 * Service layer responsible for Leave Request operations.
 *
 * Responsibilities:
 *  - Apply leave
 *  - Validate business rules
 *  - Verify leave balance
 *  - Fetch paginated leave history
 *
 * Transactional:
 *  - Ensures atomic execution of leave request creation.
 *
 * Logging Strategy:
 *  - info  → Major business actions
 *  - debug → Internal processing steps
 *  - warn  → Business validation warnings
 *  - error → Unexpected failures
 */
@Service
@Slf4j
@Transactional
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeesRepository employeesRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestMapper leaveRequestMapper;

    public LeaveRequestServiceImpl(
            LeaveRequestRepository leaveRequestRepository,
            EmployeesRepository employeesRepository,
            LeaveBalanceRepository leaveBalanceRepository,
            LeaveTypeRepository leaveTypeRepository,
            LeaveRequestMapper leaveRequestMapper) {

        this.leaveRequestRepository = leaveRequestRepository;
        this.employeesRepository = employeesRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveRequestMapper = leaveRequestMapper;
    }

    /**
     * Applies leave for an employee.
     *
     * Validations:
     *  - Date range validation
     *  - Employee existence
     *  - Leave type validation
     *  - Leave balance availability
     *  - Working days calculation (excludes weekends)
     */
    @Override
    public LeaveRequestDTO applyLeave(Long employeeId,
                                      String leaveTypeName,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      String reason) {

        log.info("Applying leave for employeeId: {}", employeeId);

        if (endDate.isBefore(startDate)) {
            log.warn("Invalid date range: {} - {}", startDate, endDate);
            throw new InvalidRequestException("End date cannot be before start date");
        }

        Employee employee = employeesRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", employeeId);
                    return new ResourceNotFoundException(
                            "Employee not found with id: " + employeeId);
                });

        LeaveTypeEnum leaveTypeEnum;
        try {
            leaveTypeEnum = LeaveTypeEnum.valueOf(leaveTypeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid leave type received: {}", leaveTypeName);
            throw new InvalidRequestException("Invalid leave type: " + leaveTypeName);
        }

        LeaveType leaveType = leaveTypeRepository.findByName(leaveTypeEnum)
                .orElseThrow(() -> {
                    log.error("Leave type not configured: {}", leaveTypeEnum);
                    return new InvalidRequestException("Leave type not configured");
                });

        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeAndLeaveType(employee, leaveType)
                .orElseThrow(() -> {
                    log.error("Leave balance not found for employeeId: {}", employeeId);
                    return new InvalidRequestException("Leave balance not found");
                });

        int days = calculateWorkingDays(startDate, endDate);

        log.debug("Calculated working days: {}", days);

        if (days <= 0) {
            log.warn("Selected dates contain no working days");
            throw new InvalidRequestException("Selected dates contain no working days");
        }

        if (balance.getRemainingDays() < days) {
            log.warn("Insufficient leave balance. Available: {}, Requested: {}",
                    balance.getRemainingDays(), days);
            throw new InvalidRequestException("Insufficient leave balance");
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

        return leaveRequestMapper.toDTO(saved);
    }

    /**
     * Fetches paginated leave history for an employee.
     */
    @Override
    public Page<LeaveRequestDTO> getLeaveHistory(Long employeeId, int page, int size) {

        log.info("Fetching leave history for employeeId: {}", employeeId);

        if (!employeesRepository.existsById(employeeId)) {
            log.error("Employee not found while fetching history. ID: {}", employeeId);
            throw new ResourceNotFoundException(
                    "Employee not found with id: " + employeeId);
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<LeaveRequest> leavePage =
                leaveRequestRepository.findByEmployeeId(employeeId, pageable);

        log.debug("Fetched {} leave records", leavePage.getNumberOfElements());

        return leavePage.map(leaveRequestMapper::toDTO);
    }

    /**
     * Calculates working days between two dates.
     * Excludes Saturday and Sunday.
     */
    private int calculateWorkingDays(LocalDate startDate, LocalDate endDate) {

        int workingDays = 0;
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {

            switch (date.getDayOfWeek()) {
                case SATURDAY:
                case SUNDAY:
                    break;
                default:
                    workingDays++;
            }

            date = date.plusDays(1);
        }

        return workingDays;
    }
}