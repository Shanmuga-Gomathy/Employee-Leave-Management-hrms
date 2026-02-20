package com.example.hrms.mapper;

import com.example.hrms.dto.LeaveRequestDTO;
import com.example.hrms.entity.LeaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * LeaveRequestMapper
 *
 * Converts LeaveRequest entity → LeaveRequestDTO.
 * Managed by Spring.
 */
@Mapper(componentModel = "spring")
public interface LeaveRequestMapper {

    /**
     * Custom mapping:
     * employee.id → employeeId
     *
     * Other fields (leaveType, dates, status, reason)
     * are automatically mapped because names match.
     */
    @Mapping(source = "employee.id", target = "employeeId")
    LeaveRequestDTO toDTO(LeaveRequest leaveRequest);
}