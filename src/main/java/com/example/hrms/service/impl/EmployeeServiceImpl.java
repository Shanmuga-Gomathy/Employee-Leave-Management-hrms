package com.example.hrms.service.impl;

import com.example.hrms.dto.EmployeeRequestDTO;
import com.example.hrms.dto.EmployeeResponseDTO;
import com.example.hrms.entity.*;
import com.example.hrms.exception.ResourceNotFoundException;
import com.example.hrms.mapper.EmployeeMapper;
import com.example.hrms.repository.EmployeesRepository;
import com.example.hrms.repository.LeaveBalanceRepository;
import com.example.hrms.repository.LeaveTypeRepository;
import com.example.hrms.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * EmployeeServiceImpl
 *
 * Service implementation for Employee operations.
 *
 * Responsibilities:
 *  - Create employee
 *  - Initialize leave balances based on department
 *  - Fetch employees with pagination
 *  - Fetch employee by ID
 *
 * Logging Strategy:
 *  - info  → Major business operations
 *  - debug → Internal processing details
 *  - warn  → Suspicious but expected scenarios
 *  - error → Unexpected failures
 */
@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeesRepository repository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeesRepository repository,
                               LeaveTypeRepository leaveTypeRepository,
                               LeaveBalanceRepository leaveBalanceRepository,
                               EmployeeMapper employeeMapper) {
        this.repository = repository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.employeeMapper = employeeMapper;
    }

    /**
     * Creates a new employee and initializes leave balances.
     */
    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO request) {

        log.info("Starting employee creation process for email: {}", request.getEmail());

        try {
            // Convert DTO to Entity
            Employee employee = employeeMapper.toEntity(request);
            employee.setActive(true);

            log.debug("Saving employee entity to database");
            Employee saved = repository.save(employee);

            log.debug("Initializing leave balances for employee ID: {}", saved.getId());
            initializeLeaveBalances(saved);

            log.info("Employee created successfully with ID: {}", saved.getId());

            return employeeMapper.toResponseDTO(saved);

        } catch (Exception ex) {
            log.error("Error occurred while creating employee with email: {}",
                    request.getEmail(), ex);
            throw ex;
        }
    }

    /**
     * Initializes leave balances based on employee department.
     */
    private void initializeLeaveBalances(Employee employee) {

        log.debug("Fetching all leave types for initialization");

        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();

        for (LeaveType leaveType : leaveTypes) {

            int days = getInitialDays(employee.getDepartment(), leaveType.getName());

            if (days == 0) {
                log.debug("No initial leave assigned for type: {} and department: {}",
                        leaveType.getName(), employee.getDepartment());
                continue;
            }

            LeaveBalance balance = new LeaveBalance();
            balance.setEmployee(employee);
            balance.setLeaveType(leaveType);
            balance.setRemainingDays(days);

            leaveBalanceRepository.save(balance);

            log.debug("Assigned {} days of {} leave to employee ID: {}",
                    days, leaveType.getName(), employee.getId());
        }
    }

    /**
     * Returns initial leave days based on department and leave type.
     */
    private int getInitialDays(Department department, LeaveTypeEnum leaveTypeEnum) {

        switch (department) {
            case CONSULTING:
            case SUPPORT:
            case DEVELOPMENT:
                if (leaveTypeEnum == LeaveTypeEnum.SICK) return 6;
                if (leaveTypeEnum == LeaveTypeEnum.CASUAL) return 6;
                if (leaveTypeEnum == LeaveTypeEnum.EARNED) return 3;
                break;

            case TRAINEE:
                if (leaveTypeEnum == LeaveTypeEnum.SICK) return 6;
                if (leaveTypeEnum == LeaveTypeEnum.CASUAL) return 6;
                if (leaveTypeEnum == LeaveTypeEnum.EARNED) return 0;
                break;
        }

        log.warn("No leave configuration found for Department: {} and LeaveType: {}",
                department, leaveTypeEnum);

        return 0;
    }

    /**
     * Fetches all employees with pagination.
     */
    @Override
    public Page<EmployeeResponseDTO> getAllEmployees(int page, int size) {

        log.info("Fetching employees - Page: {}, Size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<EmployeeResponseDTO> result = repository.findAll(pageable)
                .map(employeeMapper::toResponseDTO);

        log.debug("Fetched {} employees from database", result.getNumberOfElements());

        return result;
    }

    /**
     * Fetches employee by ID.
     */
    @Override
    public EmployeeResponseDTO getEmployeeById(Long id) {

        log.info("Fetching employee with ID: {}", id);

        Employee employee = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException(
                            "Employee not found with id: " + id);
                });

        log.debug("Employee found with ID: {}", id);

        return employeeMapper.toResponseDTO(employee);
    }
}