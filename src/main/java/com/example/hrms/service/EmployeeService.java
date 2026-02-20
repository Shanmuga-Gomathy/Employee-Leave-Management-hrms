package com.example.hrms.service;

import com.example.hrms.dto.EmployeeRequestDTO;
import com.example.hrms.dto.EmployeeResponseDTO;
import org.springframework.data.domain.Page;
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

    EmployeeResponseDTO createEmployee(EmployeeRequestDTO request);

    Page<EmployeeResponseDTO> getAllEmployees(int page, int size);

    EmployeeResponseDTO getEmployeeById(Long id);
}