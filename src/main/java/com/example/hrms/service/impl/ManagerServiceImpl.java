package com.example.hrms.service.impl;

import com.example.hrms.dto.LeaveRequestDTO;
import com.example.hrms.entity.LeaveBalance;
import com.example.hrms.entity.LeaveRequest;
import com.example.hrms.entity.LeaveStatus;
import com.example.hrms.exception.InvalidRequestException;
import com.example.hrms.exception.ResourceNotFoundException;
import com.example.hrms.mapper.LeaveRequestMapper;
import com.example.hrms.repository.LeaveBalanceRepository;
import com.example.hrms.repository.LeaveRequestRepository;
import com.example.hrms.service.ManagerService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * ManagerServiceImpl
 *
 * Service layer responsible for Manager operations.
 *
 * Responsibilities:
 *  - View all pending leave requests (Paginated)
 *  - Approve leave requests (with balance deduction)
 *  - Reject leave requests
 *
 * Security:
 *  - These operations are intended to be accessed only by MANAGER role.
 *
 * Logging Strategy:
 *  - info  → Major business operations
 *  - debug → Internal processing steps
 *  - warn  → Business rule violations
 *  - error → Resource not found / critical issues
 */
@Service
@Slf4j
public class ManagerServiceImpl implements ManagerService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestMapper leaveRequestMapper;

    public ManagerServiceImpl(LeaveRequestRepository leaveRequestRepository,
                              LeaveBalanceRepository leaveBalanceRepository,
                              LeaveRequestMapper leaveRequestMapper) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveRequestMapper = leaveRequestMapper;
    }

    /**
     * Fetch all pending leave requests (Paginated).
     */
    @Override
    public Page<LeaveRequestDTO> getPendingRequests(int page, int size) {

        log.info("Fetching pending leave requests - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<LeaveRequest> pendingPage =
                leaveRequestRepository.findByStatus(LeaveStatus.PENDING, pageable);

        log.debug("Fetched {} pending requests", pendingPage.getNumberOfElements());

        return pendingPage.map(leaveRequestMapper::toDTO);
    }

    /**
     * Approves leave request and deducts leave balance.
     *
     * Transactional:
     *  - Ensures balance deduction and status update
     *    happen atomically.
     */
    @Transactional
    @Override
    public LeaveRequestDTO approveLeave(Long requestId) {

        log.info("Attempting to approve leave request with ID: {}", requestId);

        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Leave request not found with id: {}", requestId);
                    return new ResourceNotFoundException(
                            "Leave request not found with id: " + requestId);
                });

        if (request.getStatus() != LeaveStatus.PENDING) {
            log.warn("Leave request already processed. Current status: {}", request.getStatus());
            throw new InvalidRequestException("Leave already processed");
        }

        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeAndLeaveType(
                        request.getEmployee(),
                        request.getLeaveType()
                )
                .orElseThrow(() -> {
                    log.error("Leave balance not found for employee during approval");
                    return new ResourceNotFoundException("Leave balance not found");
                });

        if (balance.getRemainingDays() < request.getTotalDays()) {
            log.warn("Insufficient balance during approval. Available: {}, Required: {}",
                    balance.getRemainingDays(), request.getTotalDays());
            throw new InvalidRequestException("Insufficient leave balance during approval");
        }

        // Deduct balance
        int updatedBalance = balance.getRemainingDays() - request.getTotalDays();
        balance.setRemainingDays(updatedBalance);
        leaveBalanceRepository.save(balance);

        log.debug("Leave balance updated. New balance: {}", updatedBalance);

        request.setStatus(LeaveStatus.APPROVED);
        LeaveRequest saved = leaveRequestRepository.save(request);

        log.info("Leave request approved successfully for ID: {}", requestId);

        return leaveRequestMapper.toDTO(saved);
    }

    /**
     * Rejects leave request.
     */
    @Override
    public LeaveRequestDTO rejectLeave(Long requestId) {

        log.info("Attempting to reject leave request with ID: {}", requestId);

        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Leave request not found with id: {}", requestId);
                    return new ResourceNotFoundException(
                            "Leave request not found with id: " + requestId);
                });

        if (request.getStatus() != LeaveStatus.PENDING) {
            log.warn("Leave request already processed. Current status: {}", request.getStatus());
            throw new InvalidRequestException("Leave already processed");
        }

        request.setStatus(LeaveStatus.REJECTED);
        LeaveRequest saved = leaveRequestRepository.save(request);

        log.info("Leave request rejected successfully for ID: {}", requestId);

        return leaveRequestMapper.toDTO(saved);
    }
}