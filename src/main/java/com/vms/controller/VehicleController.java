package com.vms.controller;

import com.vms.dto.request.TransferRequest;
import com.vms.dto.request.VehicleRequest;
import com.vms.dto.response.OwnershipHistoryResponse;
import com.vms.dto.response.VehicleResponse;
import com.vms.model.PlateNumber;
import com.vms.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> registerVehicle(@Valid @RequestBody VehicleRequest vehicleRequest) {
        return ResponseEntity.ok(vehicleService.registerVehicle(vehicleRequest));
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleResponse> getVehicleDetails(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(vehicleService.mapToVehicleResponse(
                vehicleService.getVehicleById(vehicleId)));
    }

    @PostMapping("/{vehicleId}/transfer")
    public ResponseEntity<VehicleResponse> transferVehicle(
            @PathVariable Long vehicleId,
            @Valid @RequestBody TransferRequest transferRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(vehicleService.transferVehicle(
                vehicleId, transferRequest, userDetails.getUsername()));
    }

    @GetMapping("/{vehicleId}/ownership-history")
    public ResponseEntity<List<OwnershipHistoryResponse>> getOwnershipHistory(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(vehicleService.getOwnershipHistory(vehicleId));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchVehicle(
            @RequestParam(required = false) String nationalId,
            @RequestParam(required = false) PlateNumber plateNumber,
            @RequestParam(required = false) String chassisNumber) {

        if (chassisNumber != null) {
            Optional<VehicleResponse> vehicle = vehicleService.findByChassisNumber(chassisNumber);
            return vehicle.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else if (plateNumber != null) {
            Optional<VehicleResponse> vehicle = vehicleService.findByPlateNumber(plateNumber);
            return vehicle.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else if (nationalId != null) {
            return ResponseEntity.ok(vehicleService.findByOwnerNationalId(nationalId));
        } else {
            return ResponseEntity.badRequest().body("Please provide search criteria");
        }
    }
}
