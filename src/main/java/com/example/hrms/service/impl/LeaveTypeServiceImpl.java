package com.example.hrms.service.impl;

import com.example.hrms.entity.LeaveType;
import com.example.hrms.entity.LeaveTypeEnum;
import com.example.hrms.repository.LeaveTypeRepository;
import com.example.hrms.service.LeaveTypeService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * LeaveTypeServiceImpl
 *
 * Service responsible for initializing default Leave Types.
 *
 * Purpose:
 *  - Ensures required leave types exist in database
 *  - Prevents manual database setup
 *
 * Execution:
 *  - Runs automatically when application starts
 *  - Uses @PostConstruct (executed after Spring bean initialization)
 *
 * Default Leave Types:
 *  - SICK
 *  - CASUAL
 *  - EARNED
 */
@Service
@Slf4j
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository repository;

    public LeaveTypeServiceImpl(LeaveTypeRepository repository) {
        this.repository = repository;
    }

    /**
     * Initializes leave types at application startup.
     *
     * If a leave type does not exist in database,
     * it will be inserted.
     */
    @Override
    @PostConstruct
    public void initializeLeaveTypes() {

        log.info("Initializing default leave types...");

        for (LeaveTypeEnum type : LeaveTypeEnum.values()) {

            if (repository.findByName(type).isEmpty()) {

                LeaveType leaveType = new LeaveType();
                leaveType.setName(type);

                repository.save(leaveType);

                log.debug("Inserted leave type: {}", type);

            } else {
                log.debug("Leave type already exists: {}", type);
            }
        }

        log.info("Leave type initialization completed.");
    }
}