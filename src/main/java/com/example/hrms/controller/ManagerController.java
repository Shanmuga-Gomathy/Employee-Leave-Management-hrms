package com.example.hrms.controller;

import com.example.hrms.dto.LeaveRequestDTO;
import com.example.hrms.service.ManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 This controller handles all manager related APIs.

 It is used by the manager to:
 - View all pending leave requests
 - Approve a leave request
 - Reject a leave request

 These APIs are secured and require authentication.
*/

@RestController
@RequestMapping("/manager-api/v1")
@Slf4j
public class ManagerController {

    private final ManagerService service;

    public ManagerController(ManagerService service){
        this.service = service;
    }

    /*
     This API returns all leave requests
     which are in PENDING status.
    */
    @GetMapping("/pending")
    public List<LeaveRequestDTO> getPending() {

        log.info("Manager requested pending leave list");

        List<LeaveRequestDTO> pending = service.getPendingRequests();

        log.info("Total pending leave requests: {}", pending.size());

        return pending;
    }

    /*
     This API is used to approve a leave request
     based on request ID.
    */
    @PatchMapping("/approve/{id}")
    public LeaveRequestDTO approve(@PathVariable Long id) {

        log.info("Manager approving leave request with ID: {}", id);

        LeaveRequestDTO response = service.approveLeave(id);

        log.info("Leave request {} approved successfully", id);

        return response;
    }

    /*
     This API is used to reject a leave request
     based on request ID.
    */
    @PatchMapping("/reject/{id}")
    public LeaveRequestDTO reject(@PathVariable Long id) {

        log.info("Manager rejecting leave request with ID: {}", id);

        LeaveRequestDTO response = service.rejectLeave(id);

        log.info("Leave request {} rejected successfully", id);

        return response;
    }
}