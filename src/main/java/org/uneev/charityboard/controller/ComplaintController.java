package org.uneev.charityboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uneev.charityboard.dto.ComplaintCreationDto;
import org.uneev.charityboard.dto.ResponseInfoDto;
import org.uneev.charityboard.service.ComplaintService;

import java.security.Principal;

@RestController
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<ResponseInfoDto> createComplaint(
            @RequestBody ComplaintCreationDto complaintCreationDto,
            Principal principal
    ) {
        complaintService.createComplaint(complaintCreationDto, principal.getName());
        return new ResponseEntity<>(
                new ResponseInfoDto(HttpStatus.CREATED.value(), "Complaint submitted!"),
                HttpStatus.CREATED
        );
    }
}

