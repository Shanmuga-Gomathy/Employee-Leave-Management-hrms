package com.example.hrms.service.impl;

import com.example.hrms.entity.LeaveType;
import com.example.hrms.entity.LeaveTypeEnum;
import com.example.hrms.repository.LeaveTypeRepository;
import com.example.hrms.service.LeaveTypeService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

/**
 * LeaveTypeServiceImpl
 *
 * Initializes default leave types in the database.
 * - Runs automatically at application startup
 * - Inserts SICK, CASUAL, EARNED if not already present
 */
@Service
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository repository;

    public LeaveTypeServiceImpl(LeaveTypeRepository repository) {
        this.repository = repository;
    }

    // Initialize leave types on application startup
    @Override
    @PostConstruct
    public void initializeLeaveTypes() {

        for (LeaveTypeEnum type : LeaveTypeEnum.values()) {

            if (repository.findByName(type).isEmpty()) {

                LeaveType leaveType = new LeaveType();
                leaveType.setName(type);

                repository.save(leaveType);
            }
        }
    }
}