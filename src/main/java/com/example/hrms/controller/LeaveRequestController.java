package com.example.hrms.controller;

import com.example.hrms.dto.LeaveRequestDTO;
import com.example.hrms.service.LeaveRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/*
 This controller handles leave request related APIs.

 It is used to:
 - Apply for leave
 - View leave history of an employee

 This controller calls LeaveRequestService to handle business logic.
*/

@RestController
@RequestMapping("/leave-request-api/v1")
@Slf4j
public class LeaveRequestController {

    private final LeaveRequestService service;

    public LeaveRequestController(LeaveRequestService service) {
        this.service = service;
    }

    /*
     This API is used to apply for leave.
     It takes employeeId, leaveType, startDate, endDate and reason.
    */
    @PostMapping("/apply")
    public LeaveRequestDTO applyLeave(@RequestParam Long employeeId,
                                      @RequestParam String leaveType,
                                      @RequestParam String startDate,
                                      @RequestParam String endDate,
                                      @RequestParam String reason) {

        log.info("Leave apply request received for employeeId: {}, type: {}",
                employeeId, leaveType);

        LeaveRequestDTO response = service.applyLeave(
                employeeId,
                leaveType,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate),
                reason
        );

        log.info("Leave applied successfully with requestId: {}", response.getId());

        return response;
    }

    /*
     This API returns leave history for a specific employee.
    */
    @GetMapping("/history")
    public List<LeaveRequestDTO> getHistory(@RequestParam Long employeeId) {

        log.info("Fetching leave history for employeeId: {}", employeeId);

        List<LeaveRequestDTO> history = service.getLeaveHistory(employeeId);

        log.info("Total leave records found: {}", history.size());

        return history;
    }
}