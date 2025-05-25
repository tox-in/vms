package com.vms.controller;

import com.vms.dto.request.OwnerRequest;
import com.vms.dto.request.PlateNumberRequest;
import com.vms.dto.response.OwnerResponse;
import com.vms.dto.response.PlateNumberResponse;
import com.vms.service.OwnerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class OwnerController {
    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @PostMapping
    public ResponseEntity<OwnerResponse> registerOwner(@Valid @RequestBody OwnerRequest ownerRequest) {
        return ResponseEntity.ok(ownerService.registerOwner(ownerRequest));
    }

    @GetMapping
    public ResponseEntity<Page<OwnerResponse>> getAllOwners(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(ownerService.getAllOwners(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<OwnerResponse>> searchOwners(
            @RequestParam String query,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(ownerService.searchOwners(query, pageable));
    }

    @PostMapping("/{ownerId}/plate-numbers")
    public ResponseEntity<PlateNumberResponse> registerPlateNumber(
            @PathVariable Long ownerId,
            @Valid @RequestBody PlateNumberRequest plateNumberRequest) {
        return ResponseEntity.ok(ownerService.registerPlateNumber(ownerId, plateNumberRequest));
    }

    @GetMapping("/{ownerId}/plate-numbers")
    public ResponseEntity<List<PlateNumberResponse>> getOwnerPlateNumbers(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ownerService.getOwnerPlateNumbers(ownerId));
    }
}
