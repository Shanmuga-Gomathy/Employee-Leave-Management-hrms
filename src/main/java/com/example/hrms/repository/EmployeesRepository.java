package com.example.hrms.repository;

import com.example.hrms.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * EmployeesRepository
 *
 * Handles database operations for Employee entity.
 * Provides basic CRUD operations using JPA.
 */
public interface EmployeesRepository extends JpaRepository<Employee,Long> {
}
