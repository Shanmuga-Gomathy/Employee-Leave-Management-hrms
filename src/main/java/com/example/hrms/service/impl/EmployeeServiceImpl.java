package com.example.hrms.service.impl;

import com.example.hrms.dto.EmployeeRequestDTO;
import com.example.hrms.dto.EmployeeResponseDTO;
import com.example.hrms.entity.*;
import com.example.hrms.exception.ResourceNotFoundException;
import com.example.hrms.repository.EmployeesRepository;
import com.example.hrms.repository.LeaveBalanceRepository;
import com.example.hrms.repository.LeaveTypeRepository;
import com.example.hrms.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * EmployeeServiceImpl
 *
 * Handles employee business logic.
 * - Creates employees
 * - Initializes leave balances
 * - Fetches employee details
 * - Converts Entity to DTO
 */
@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeesRepository repository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    public EmployeeServiceImpl(EmployeesRepository repository,
                               LeaveTypeRepository leaveTypeRepository,
                               LeaveBalanceRepository leaveBalanceRepository) {
        this.repository = repository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    // Create new employee and initialize leave balances
    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO request) {

        log.info("Creating employee with email: {}", request.getEmail());

        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(request.getDepartment());
        employee.setActive(true);

        Employee saved = repository.save(employee);
        log.info("Employee saved with ID: {}", saved.getId());

        initializeLeaveBalances(saved);

        return mapToResponse(saved);
    }

    // Initialize leave balances based on department rules
    private void initializeLeaveBalances(Employee employee) {

        log.info("Initializing leave balances for employee ID: {}", employee.getId());

        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();

        for (LeaveType leaveType : leaveTypes) {

            int days = getInitialDays(employee.getDepartment(), leaveType.getName());

            if (days == 0) {
                log.warn("Skipping leave type {} for employee ID {} (0 days)",
                        leaveType.getName(), employee.getId());
                continue;
            }

            LeaveBalance balance = new LeaveBalance();
            balance.setEmployee(employee);
            balance.setLeaveType(leaveType);
            balance.setRemainingDays(days);

            leaveBalanceRepository.save(balance);

            log.info("Initialized {} days for leave type {} for employee ID {}",
                    days, leaveType.getName(), employee.getId());
        }
    }

    // Define initial leave allocation rules
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

        return 0;
    }

    // Fetch all employees
    @Override
    public List<EmployeeResponseDTO> getAllEmployees() {

        log.info("Fetching all employees");

        List<EmployeeResponseDTO> employees = repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Total employees fetched: {}", employees.size());

        return employees;
    }

    // Fetch employee by ID
    @Override
    public EmployeeResponseDTO getEmployeeById(Long id) {

        log.info("Fetching employee with ID: {}", id);

        Employee employee = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException("Employee not found with id: " + id);
                });

        return mapToResponse(employee);
    }

    // Convert Employee entity to Response DTO
    private EmployeeResponseDTO mapToResponse(Employee employee) {

        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.isActive()
        );
    }
}