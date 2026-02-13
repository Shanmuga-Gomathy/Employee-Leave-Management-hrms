package com.example.hrms.service.impl;

import com.example.hrms.dto.LeaveRequestDTO;
import com.example.hrms.entity.LeaveBalance;
import com.example.hrms.entity.LeaveRequest;
import com.example.hrms.entity.LeaveStatus;
import com.example.hrms.exception.InvalidRequestException;
import com.example.hrms.exception.ResourceNotFoundException;
import com.example.hrms.repository.LeaveBalanceRepository;
import com.example.hrms.repository.LeaveRequestRepository;
import com.example.hrms.service.ManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ManagerServiceImpl
 *
 * Handles manager operations:
 *  - View pending leave requests
 *  - Approve leave requests
 *  - Reject leave requests
 *
 * These operations are secured and accessible only by MANAGER role.
 */
@Service
@Slf4j
public class ManagerServiceImpl implements ManagerService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    public ManagerServiceImpl(LeaveRequestRepository leaveRequestRepository,
                              LeaveBalanceRepository leaveBalanceRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
    }

    // Fetch all pending leave requests
    @Override
    public List<LeaveRequestDTO> getPendingRequests() {

        log.info("Fetching all pending leave requests");

        List<LeaveRequestDTO> pendingList = leaveRequestRepository
                .findByStatus(LeaveStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        log.info("Total pending requests found: {}", pendingList.size());

        return pendingList;
    }

    // Approve leave request and deduct balance
    @Override
    public LeaveRequestDTO approveLeave(Long requestId) {

        log.info("Attempting to approve leave request with ID: {}", requestId);

        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Leave request not found with ID: {}", requestId);
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
                    log.error("Leave balance not found for employee ID: {}",
                            request.getEmployee().getId());
                    return new ResourceNotFoundException("Leave balance not found");
                });

        balance.setRemainingDays(
                balance.getRemainingDays() - request.getTotalDays()
        );

        leaveBalanceRepository.save(balance);

        request.setStatus(LeaveStatus.APPROVED);

        LeaveRequest saved = leaveRequestRepository.save(request);

        log.info("Leave request approved successfully for ID: {}", requestId);

        return convertToDTO(saved);
    }

    // Reject leave request
    @Override
    public LeaveRequestDTO rejectLeave(Long requestId) {

        log.info("Attempting to reject leave request with ID: {}", requestId);

        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Leave request not found with ID: {}", requestId);
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

        return convertToDTO(saved);
    }

    // Convert Entity to DTO
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