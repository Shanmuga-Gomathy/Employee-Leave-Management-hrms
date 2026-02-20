package com.example.hrms.service;

import com.example.hrms.dto.LeaveRequestDTO;
import com.example.hrms.entity.*;
import com.example.hrms.exception.InvalidRequestException;
import com.example.hrms.exception.ResourceNotFoundException;
import com.example.hrms.mapper.LeaveRequestMapper;
import com.example.hrms.repository.*;
import com.example.hrms.service.impl.LeaveRequestServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * LeaveRequestServiceImplTest
 *
 * Unit test class for LeaveRequestServiceImpl.
 *
 * Tests:
 *  - Apply leave (success and failure scenarios)
 *  - Validate business rules
 *  - Fetch leave history with pagination
 *
 * Uses:
 *  - JUnit 5
 *  - Mockito for mocking dependencies
 */
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class LeaveRequestServiceImplTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeesRepository employeesRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private LeaveRequestMapper leaveRequestMapper;

    @InjectMocks
    private LeaveRequestServiceImpl leaveRequestService;

    private Employee employee;
    private LeaveType leaveType;
    private LeaveBalance balance;
    private LeaveRequest leaveRequest;
    private LeaveRequestDTO dto;

    @BeforeEach
    void setup() {
        employee = new Employee();
        employee.setId(1L);

        leaveType = new LeaveType();
        leaveType.setName(LeaveTypeEnum.SICK);

        balance = new LeaveBalance();
        balance.setRemainingDays(10);

        leaveRequest = new LeaveRequest();
        leaveRequest.setId(100L);

        dto = new LeaveRequestDTO();
        dto.setId(100L);
    }

    /**
     * Tests successful leave application.
     */
    @Test
    void applyLeave_ShouldSucceed() {

        when(employeesRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        when(leaveTypeRepository.findByName(LeaveTypeEnum.SICK))
                .thenReturn(Optional.of(leaveType));

        when(leaveBalanceRepository.findByEmployeeAndLeaveType(employee, leaveType))
                .thenReturn(Optional.of(balance));

        when(leaveRequestRepository.save(any(LeaveRequest.class)))
                .thenReturn(leaveRequest);

        when(leaveRequestMapper.toDTO(leaveRequest))
                .thenReturn(dto);

        LeaveRequestDTO result = leaveRequestService.applyLeave(
                1L,
                "SICK",
                LocalDate.of(2026, 2, 23),
                LocalDate.of(2026, 2, 24),
                "Fever"
        );

        assertNotNull(result);
        assertEquals(100L, result.getId());

        verify(leaveRequestRepository, times(1)).save(any());
    }

    /**
     * Tests leave application when end date is before start date.
     */
    @Test
    void applyLeave_ShouldThrow_WhenEndBeforeStart() {

        assertThrows(InvalidRequestException.class,
                () -> leaveRequestService.applyLeave(
                        1L,
                        "SICK",
                        LocalDate.of(2026, 2, 25),
                        LocalDate.of(2026, 2, 24),
                        "Reason"
                ));
    }

    /**
     * Tests leave application when employee does not exist.
     */
    @Test
    void applyLeave_ShouldThrow_WhenEmployeeNotFound() {

        when(employeesRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> leaveRequestService.applyLeave(
                        1L,
                        "SICK",
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        "Reason"
                ));
    }

    /**
     * Tests leave application when leave balance is insufficient.
     */
    @Test
    void applyLeave_ShouldThrow_WhenInsufficientBalance() {

        balance.setRemainingDays(1);

        when(employeesRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        when(leaveTypeRepository.findByName(LeaveTypeEnum.SICK))
                .thenReturn(Optional.of(leaveType));

        when(leaveBalanceRepository.findByEmployeeAndLeaveType(employee, leaveType))
                .thenReturn(Optional.of(balance));

        assertThrows(InvalidRequestException.class,
                () -> leaveRequestService.applyLeave(
                        1L,
                        "SICK",
                        LocalDate.of(2026, 2, 23),
                        LocalDate.of(2026, 2, 28),
                        "Vacation"
                ));
    }

    /**
     * Tests fetching leave history with pagination.
     */
    @Test
    void getLeaveHistory_ShouldReturnPage() {

        when(employeesRepository.existsById(1L))
                .thenReturn(true);

        Page<LeaveRequest> page =
                new PageImpl<>(java.util.List.of(leaveRequest));

        when(leaveRequestRepository.findByEmployeeId(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        when(leaveRequestMapper.toDTO(leaveRequest))
                .thenReturn(dto);

        Page<LeaveRequestDTO> result =
                leaveRequestService.getLeaveHistory(1L, 0, 5);

        assertEquals(1, result.getTotalElements());
        verify(leaveRequestRepository).findByEmployeeId(eq(1L), any(Pageable.class));
    }
}