package com.example.hrms.controller;

import com.example.hrms.dto.EmployeeRequestDTO;
import com.example.hrms.dto.EmployeeResponseDTO;
import com.example.hrms.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 This controller handles all employee related APIs.

 It is used to:
 - Create a new employee
 - Get all employees
 - Get employee by ID

 This controller calls EmployeeService to perform business logic.
*/

@RestController
@RequestMapping("/employee-Api/v1")
@Slf4j
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    /*
     This API is used to create a new employee.
     It takes employee details in request body.
    */
    @PostMapping("/employee")
    public EmployeeResponseDTO createEmployee(
            @Valid @RequestBody EmployeeRequestDTO request) {

        log.info("Creating employee with email: {}", request.getEmail());

        EmployeeResponseDTO response = service.createEmployee(request);

        log.info("Employee created successfully with ID: {}", response.getId());

        return response;
    }

    /*
     This API returns the list of all employees.
    */
    @GetMapping("/employees")
    public List<EmployeeResponseDTO> getAllEmployees() {

        log.info("Fetching all employees");

        List<EmployeeResponseDTO> employees = service.getAllEmployees();

        log.info("Total employees fetched: {}", employees.size());

        return employees;
    }

    /*
     This API returns employee details by ID.
    */
    @GetMapping("/employee/{id}")
    public EmployeeResponseDTO getEmployee(@PathVariable Long id) {

        log.info("Fetching employee with ID: {}", id);

        EmployeeResponseDTO response = service.getEmployeeById(id);

        log.info("Employee fetched successfully for ID: {}", id);

        return response;
    }
}