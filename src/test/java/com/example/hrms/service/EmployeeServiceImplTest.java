package com.example.hrms.service;

import com.example.hrms.dto.EmployeeRequestDTO;
import com.example.hrms.dto.EmployeeResponseDTO;
import com.example.hrms.entity.Department;
import com.example.hrms.entity.Employee;
import com.example.hrms.exception.ResourceNotFoundException;
import com.example.hrms.mapper.EmployeeMapper;
import com.example.hrms.repository.EmployeesRepository;
import com.example.hrms.repository.LeaveBalanceRepository;
import com.example.hrms.repository.LeaveTypeRepository;
import com.example.hrms.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * EmployeeServiceImplTest
 *
 * Unit test class for EmployeeServiceImpl.
 *
 * Tests:
 *  - Create employee
 *  - Get employee by ID
 *  - Get all employees with pagination
 *
 * Uses:
 *  - JUnit 5
 *  - Mockito for mocking dependencies
 */
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeesRepository repository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeRequestDTO requestDTO;
    private EmployeeResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setName("Test");
        employee.setEmail("test@gmail.com");
        employee.setDepartment(Department.DEVELOPMENT);
        employee.setActive(true);

        requestDTO = new EmployeeRequestDTO();
        requestDTO.setName("Test");
        requestDTO.setEmail("test@gmail.com");
        requestDTO.setDepartment(Department.DEVELOPMENT);

        responseDTO = new EmployeeResponseDTO(
                1L,
                "Test",
                "test@gmail.com",
                Department.DEVELOPMENT,
                true
        );
    }

    /**
     * Tests successful employee creation.
     */
    @Test
    void createEmployee_ShouldReturnResponseDTO() {

        when(employeeMapper.toEntity(requestDTO)).thenReturn(employee);
        when(repository.save(employee)).thenReturn(employee);
        when(employeeMapper.toResponseDTO(employee)).thenReturn(responseDTO);
        when(leaveTypeRepository.findAll()).thenReturn(Collections.emptyList());

        EmployeeResponseDTO result = employeeService.createEmployee(requestDTO);

        assertNotNull(result);
        assertEquals("Test", result.getName());

        verify(repository, times(1)).save(employee);
    }

    /**
     * Tests fetching employee by ID when employee exists.
     */
    @Test
    void getEmployeeById_ShouldReturnEmployee() {

        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeMapper.toResponseDTO(employee)).thenReturn(responseDTO);

        EmployeeResponseDTO result = employeeService.getEmployeeById(1L);

        assertEquals(1L, result.getId());
        verify(repository).findById(1L);
    }

    /**
     * Tests fetching employee by ID when employee does not exist.
     */
    @Test
    void getEmployeeById_ShouldThrowException_WhenNotFound() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(1L));
    }

    /**
     * Tests fetching all employees with pagination.
     */
    @Test
    void getAllEmployees_ShouldReturnPagedResult() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Employee> employeePage =
                new PageImpl<>(Collections.singletonList(employee));

        when(repository.findAll(pageable)).thenReturn(employeePage);
        when(employeeMapper.toResponseDTO(employee)).thenReturn(responseDTO);

        Page<EmployeeResponseDTO> result =
                employeeService.getAllEmployees(0, 5);

        assertEquals(1, result.getTotalElements());
        verify(repository).findAll(pageable);
    }
}