package com.vms.service;

import com.vms.dto.request.TransferRequest;
import com.vms.dto.request.VehicleRequest;
import com.vms.dto.response.OwnerResponse;
import com.vms.dto.response.OwnershipHistoryResponse;
import com.vms.dto.response.PlateNumberResponse;
import com.vms.dto.response.VehicleResponse;
import com.vms.model.*;
import com.vms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final OwnerRepository ownerRepository;
    private final PlateNumberRepository plateNumberRepository;
    private final VehicleOwnershipHistoryRepository ownershipHistoryRepository;
    private final UserRepository userRepository;

    public VehicleService(VehicleRepository vehicleRepository, OwnerRepository ownerRepository, PlateNumberRepository plateNumberRepository, VehicleOwnershipHistoryRepository ownershipHistoryRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.ownerRepository = ownerRepository;
        this.plateNumberRepository = plateNumberRepository;
        this.ownershipHistoryRepository = ownershipHistoryRepository;
        this.userRepository = userRepository;
    }

    public VehicleResponse registerVehicle(VehicleRequest vehicleRequest) {
        if (vehicleRepository.existsByChassisNumber(vehicleRequest.chassisNumber())) {
            throw new RuntimeException("Vehicle with this chassis number already exists");
        }
        Owner owner = ownerRepository.findById(vehicleRequest.ownerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        PlateNumber plateNumber = plateNumberRepository.findById(vehicleRequest.plateNumberId())
                .orElseThrow(() -> new RuntimeException("Plate number not found"));

        if (!"AVAILABLE".equals(plateNumber.getStatus())) {
            throw new RuntimeException("Plate number is not available");
        }

        if (!plateNumber.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("Plate number does not belong to the specified owner");
        }

        PlateNumber updatedPlateNumber = new PlateNumber(
                plateNumber.getPlateNumber(),
                plateNumber.getOwner(),
                plateNumber.getIssuedDate(),
                "IN_USE"
        );

        plateNumberRepository.save(updatedPlateNumber);

        Vehicle vehicle = new Vehicle(
                vehicleRequest.chassisNumber(),
                vehicleRequest.manufactureCompany(),
                vehicleRequest.manufactureYear(),
                vehicleRequest.modelName(),
                vehicleRequest.price(),
                owner,
                updatedPlateNumber,
                LocalDateTime.now()
        );

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapToVehicleResponse(savedVehicle);
    }

    @Transactional
    public VehicleResponse transferVehicle(Long vehicleId, TransferRequest transferRequest, String currentUserEmail) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        Owner newOwner = ownerRepository.findById(transferRequest.newOwnerId())
                .orElseThrow(() -> new RuntimeException("New owner not found"));

        PlateNumber newPlateNumber = plateNumberRepository.findById(transferRequest.newPlateNumberId())
                .orElseThrow(() -> new RuntimeException("New plate number not found"));

        if (!"AVAILABLE".equals(newPlateNumber.getStatus())) {
            throw new RuntimeException("New plate number is not available");
        }

        if (!newPlateNumber.getOwner().getId().equals(newOwner.getId())) {
            throw new RuntimeException("New plate number does not belong to the new owner");
        }

        User transferredBy = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Mark old plate number as available
        PlateNumber oldPlateNumber = vehicle.getCurrentPlateNumber();
        PlateNumber updatedOldPlateNumber = new PlateNumber(
                oldPlateNumber.getPlateNumber(),
                oldPlateNumber.getOwner(),
                oldPlateNumber.getIssuedDate(),
                "AVAILABLE"
        );

        plateNumberRepository.save(updatedOldPlateNumber);

        // Mark new plate number as in use
        PlateNumber updatedNewPlateNumber = new PlateNumber(
                newPlateNumber.getPlateNumber(),
                newPlateNumber.getOwner(),
                newPlateNumber.getIssuedDate(),
                "IN_USE"
        );

        plateNumberRepository.save(updatedNewPlateNumber);

        // Create ownership history record
        VehicleOwnershipHistory history = new VehicleOwnershipHistory(
                vehicle,
                vehicle.getCurrentOwner(),
                newOwner,
                updatedNewPlateNumber,
                transferRequest.transferPrice(),
                LocalDateTime.now(),
                transferredBy
        );

        ownershipHistoryRepository.save(history);

        // Update vehicle with new owner and plate number
        Vehicle updatedVehicle = new Vehicle(
                vehicle.getChassisNumber(),
                vehicle.getManufactureCompany(),
                vehicle.getManufactureYear(),
                vehicle.getModelName(),
                transferRequest.transferPrice(),
                newOwner,
                updatedNewPlateNumber,
                vehicle.getRegistrationDate()
        );

        Vehicle savedVehicle = vehicleRepository.save(updatedVehicle);
        return mapToVehicleResponse(savedVehicle);
    }

    public List<OwnershipHistoryResponse> getOwnershipHistory(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        return ownershipHistoryRepository.findByVehicleOrderByTransferDateDesc(vehicle).stream()
                .map(this::mapToOwnershipHistoryResponse)
                .toList();
    }

    public Optional<VehicleResponse> findByChassisNumber(String chassisNumber) {
        return vehicleRepository.findByChassisNumber(chassisNumber)
                .map(this::mapToVehicleResponse);
    }

    public Optional<VehicleResponse> findByPlateNumber(String plateNumber) {
        return vehicleRepository.findByCurrentPlateNumber(plateNumber)
                .map(this::mapToVehicleResponse);
    }

    public List<VehicleResponse> findByOwnerNationalId(String nationalId) {
        Owner owner = ownerRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        return vehicleRepository.findByCurrentOwner(owner).stream()
                .map(this::mapToVehicleResponse)
                .toList();
    }

    public VehicleResponse mapToVehicleResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getChassisNumber(),
                vehicle.getManufactureCompany(),
                vehicle.getManufactureYear(),
                vehicle.getModelName(),
                vehicle.getCurrentPrice(),
                new OwnerResponse(
                        vehicle.getCurrentOwner().getId(),
                        vehicle.getCurrentOwner().getNames(),
                        vehicle.getCurrentOwner().getNationalId(),
                        vehicle.getCurrentOwner().getPhone(),
                        vehicle.getCurrentOwner().getAddress()
                ),
                new PlateNumberResponse(
                        vehicle.getCurrentPlateNumber().getId(),
                        vehicle.getCurrentPlateNumber().getPlateNumber(),
                        vehicle.getCurrentPlateNumber().getIssuedDate(),
                        vehicle.getCurrentPlateNumber().getStatus()
                ),
                vehicle.getRegistrationDate()
        );
    }

    private OwnershipHistoryResponse mapToOwnershipHistoryResponse(VehicleOwnershipHistory history) {
        return new OwnershipHistoryResponse(
                history.getPreviousOwner().getNames(),
                history.getNewOwner().getNames(),
                history.getPreviousPlateNumber() != null ? history.getPreviousPlateNumber().getPlateNumber() : "N/A",
                history.getNewPlateNumber().getPlateNumber(),
                history.getTransferPrice(),
                history.getTransferDate()
        );
    }

    public Vehicle getVehicleById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));
    }
}
