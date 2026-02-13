package com.example.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


/**
 * Employee Entity
 *
 * Represents an employee in the HRMS system.
 * Stores basic employee details like name, email, department and status.
 */
@Entity
@Table(name="employees")
@Getter
@Setter
public class Employee {

    //Primary key of the employee
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    //Employee full name
    private String name;

    //Unique email address of employee
    @Column(unique = true)
    private String email;

    //Department assigned to employee
    @Enumerated(EnumType.STRING)
    private Department department;

    //Indicates whether employee is active
    private boolean active = true;

    public Employee() {
    }

    public Employee(String name, String email, Department department) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.active = true;
    }

}
