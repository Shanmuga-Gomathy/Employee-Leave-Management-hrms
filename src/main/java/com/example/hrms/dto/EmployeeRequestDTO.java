package com.example.hrms.dto;

import com.example.hrms.entity.Department;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/*
 This DTO is used to receive employee data
 when creating a new employee.

 It contains:
 - Employee name
 - Employee email
 - Employee department

 Validation is applied to make sure
 the input data is correct.
*/

public class EmployeeRequestDTO {

    /*
     Employee name should not be blank.
    */
    @NotBlank
    private String name;

    /*
     Email must be valid and should not be blank.
    */
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email must not be blank")
    private String email;

    /*
     Department must not be null.
    */
    @NotNull(message = "Department must not be null")
    private Department department;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}