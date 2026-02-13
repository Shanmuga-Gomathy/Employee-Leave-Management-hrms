package com.example.hrms.dto;

import com.example.hrms.entity.Department;

/*
 This DTO is used to send employee data
 as a response to the client.

 It contains:
 - Employee ID
 - Employee name
 - Employee email
 - Employee department
 - Employee active status

 This DTO is returned after creating or fetching employees.
*/

public class EmployeeResponseDTO {

    private Long id;
    private String name;
    private String email;
    private Department department;
    private boolean active;

    /*
     Default constructor.
    */
    public EmployeeResponseDTO(){
    }

    /*
     Constructor used to set all employee details.
    */
    public EmployeeResponseDTO(Long id, String name, String email, Department department, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}