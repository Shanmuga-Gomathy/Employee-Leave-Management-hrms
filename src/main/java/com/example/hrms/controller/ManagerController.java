package com.example.hrms.controller;

import com.example.hrms.dto.LeaveRequestDTO;
import com.example.hrms.service.ManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/*
 This controller handles all manager related APIs.

 It is used by the manager to:
 - View all pending leave requests (Paginated)
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
     which are in PENDING status (Paginated).
    */
    @GetMapping("/pending")
    public Page<LeaveRequestDTO> getPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        log.info("Manager requested pending leave list | page: {}, size: {}", page, size);

        Page<LeaveRequestDTO> pendingPage = service.getPendingRequests(page, size);

        log.info("Pending leave page fetched successfully | totalElements: {}, totalPages: {}",
                pendingPage.getTotalElements(),
                pendingPage.getTotalPages());

        return pendingPage;
    }

    /*
     This API is used to approve a leave request
     based on request ID.
    */
    @PatchMapping("/approve/{id}")
    public LeaveRequestDTO approve(@PathVariable Long id) {

        log.info("Manager attempting to approve leave request | ID: {}", id);

        LeaveRequestDTO response = service.approveLeave(id);

        log.info("Leave request approved successfully | ID: {}, Status: {}",
                id, response.getStatus());

        return response;
    }

    /*
     This API is used to reject a leave request
     based on request ID.
    */
    @PatchMapping("/reject/{id}")
    public LeaveRequestDTO reject(@PathVariable Long id) {

        log.info("Manager attempting to reject leave request | ID: {}", id);

        LeaveRequestDTO response = service.rejectLeave(id);

        log.info("Leave request rejected successfully | ID: {}, Status: {}",
                id, response.getStatus());

        return response;
    }
}