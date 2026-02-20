package com.example.hrms.service;

import com.example.hrms.dto.LeaveRequestDTO;
import com.example.hrms.entity.*;
import com.example.hrms.exception.InvalidRequestException;
import com.example.hrms.exception.ResourceNotFoundException;
import com.example.hrms.mapper.LeaveRequestMapper;
import com.example.hrms.repository.LeaveBalanceRepository;
import com.example.hrms.repository.LeaveRequestRepository;
import com.example.hrms.service.impl.ManagerServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ManagerServiceImplTest
 *
 * Unit test class for ManagerServiceImpl.
 *
 * Tests:
 *  - Get pending leave requests
 *  - Approve leave (success and failure cases)
 *  - Reject leave (success and failure cases)
 *
 * Uses:
 *  - JUnit 5
 *  - Mockito for mocking dependencies
 */
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ManagerServiceImplTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private LeaveRequestMapper leaveRequestMapper;

    @InjectMocks
    private ManagerServiceImpl managerService;

    private LeaveRequest request;
    private LeaveBalance balance;
    private LeaveRequestDTO dto;

    @BeforeEach
    void setup() {

        Employee employee = new Employee();
        LeaveType leaveType = new LeaveType();

        request = new LeaveRequest();
        request.setId(1L);
        request.setStatus(LeaveStatus.PENDING);
        request.setTotalDays(3);
        request.setEmployee(employee);
        request.setLeaveType(leaveType);

        balance = new LeaveBalance();
        balance.setRemainingDays(10);

        dto = new LeaveRequestDTO();
        dto.setId(1L);
    }

    /**
     * Tests fetching pending leave requests with pagination.
     */
    @Test
    void getPendingRequests_ShouldReturnPage() {

        Page<LeaveRequest> page =
                new PageImpl<>(java.util.List.of(request));

        when(leaveRequestRepository.findByStatus(eq(LeaveStatus.PENDING), any(Pageable.class)))
                .thenReturn(page);

        when(leaveRequestMapper.toDTO(request))
                .thenReturn(dto);

        Page<LeaveRequestDTO> result =
                managerService.getPendingRequests(0, 5);

        assertEquals(1, result.getTotalElements());
        verify(leaveRequestRepository).findByStatus(eq(LeaveStatus.PENDING), any());
    }

    /**
     * Tests successful leave approval.
     */
    @Test
    void approveLeave_ShouldSucceed() {

        when(leaveRequestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        when(leaveBalanceRepository.findByEmployeeAndLeaveType(
                request.getEmployee(),
                request.getLeaveType()))
                .thenReturn(Optional.of(balance));

        when(leaveRequestRepository.save(request))
                .thenReturn(request);

        when(leaveRequestMapper.toDTO(request))
                .thenReturn(dto);

        LeaveRequestDTO result = managerService.approveLeave(1L);

        assertEquals(LeaveStatus.APPROVED, request.getStatus());
        assertEquals(7, balance.getRemainingDays());

        verify(leaveBalanceRepository).save(balance);
        verify(leaveRequestRepository).save(request);
        assertNotNull(result);
    }

    /**
     * Tests leave approval when request is not found.
     */
    @Test
    void approveLeave_ShouldThrow_WhenNotFound() {

        when(leaveRequestRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> managerService.approveLeave(1L));
    }

    /**
     * Tests leave approval when request is already processed.
     */
    @Test
    void approveLeave_ShouldThrow_WhenAlreadyProcessed() {

        request.setStatus(LeaveStatus.APPROVED);

        when(leaveRequestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        assertThrows(InvalidRequestException.class,
                () -> managerService.approveLeave(1L));
    }

    /**
     * Tests leave approval when balance is insufficient.
     */
    @Test
    void approveLeave_ShouldThrow_WhenInsufficientBalance() {

        balance.setRemainingDays(1);

        when(leaveRequestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        when(leaveBalanceRepository.findByEmployeeAndLeaveType(
                request.getEmployee(),
                request.getLeaveType()))
                .thenReturn(Optional.of(balance));

        assertThrows(InvalidRequestException.class,
                () -> managerService.approveLeave(1L));
    }

    /**
     * Tests successful leave rejection.
     */
    @Test
    void rejectLeave_ShouldSucceed() {

        when(leaveRequestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        when(leaveRequestRepository.save(request))
                .thenReturn(request);

        when(leaveRequestMapper.toDTO(request))
                .thenReturn(dto);

        LeaveRequestDTO result = managerService.rejectLeave(1L);

        assertEquals(LeaveStatus.REJECTED, request.getStatus());
        assertNotNull(result);
    }

    /**
     * Tests leave rejection when request is already processed.
     */
    @Test
    void rejectLeave_ShouldThrow_WhenAlreadyProcessed() {

        request.setStatus(LeaveStatus.APPROVED);

        when(leaveRequestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        assertThrows(InvalidRequestException.class,
                () -> managerService.rejectLeave(1L));
    }
}