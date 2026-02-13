package com.example.hrms.service;

import com.example.hrms.dto.EmployeeRequestDTO;
import com.example.hrms.dto.EmployeeResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * EmployeeService
 *
 * Defines employee-related operations:
 *  - Create employee
 *  - Get all employees
 *  - Get employee by ID
 */
@Service
public interface EmployeeService {
    // Create a new employee
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO request);

    // Fetch all employees
    List<EmployeeResponseDTO> getAllEmployees();

    // Fetch employee by ID
    EmployeeResponseDTO getEmployeeById(Long id);
}
