package com.vms.service;

import com.vms.dto.request.OwnerRequest;
import com.vms.dto.request.PlateNumberRequest;
import com.vms.dto.response.OwnerResponse;
import com.vms.dto.response.PlateNumberResponse;
import com.vms.model.Owner;
import com.vms.model.PlateNumber;
import com.vms.repository.OwnerRepository;
import com.vms.repository.PlateNumberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final PlateNumberRepository plateNumberRepository;

    public OwnerService(OwnerRepository ownerRepository,
                        PlateNumberRepository plateNumberRepository) {
        this.ownerRepository = ownerRepository;
        this.plateNumberRepository = plateNumberRepository;
    }

    public OwnerResponse registerOwner(OwnerRequest ownerRequest) {
        if (ownerRepository.existsByNationalId(ownerRequest.nationalId())) {
            throw new RuntimeException("Owner with this national ID already exists");
        }

        Owner owner = new Owner(
                ownerRequest.name(),
                ownerRequest.nationalId(),
                ownerRequest.phone(),
                ownerRequest.address()
        );

        Owner savedOwner = ownerRepository.save(owner);
        return mapToOwnerResponse(savedOwner);
    }

    public Page<OwnerResponse> getAllOwners(Pageable pageable) {
        return ownerRepository.findAll(pageable).map(this::mapToOwnerResponse);
    }

    public Page<OwnerResponse> searchOwners(String query, Pageable pageable) {
        return ownerRepository.findByNationalIdContainingOrPhoneContaining(query, query, pageable).map(this::mapToOwnerResponse);
    }

    public PlateNumberResponse registerPlateNumber(Long ownerId, PlateNumberRequest plateNumberRequest) {
        Owner owner = ownerRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("Owner not found"));

        if (plateNumberRepository.existsByPlateNumber(plateNumberRequest.plateNumber())) {
            throw new RuntimeException("Plate number already exists");
        }

        LocalDateTime issuedDate = plateNumberRequest.issuedDate() != null ? plateNumberRequest.issuedDate() : LocalDateTime.now();

        PlateNumber plateNumber = new PlateNumber(
            plateNumberRequest.plateNumber(),
                owner,
                issuedDate,
                "AVAILABLE"
        );

        PlateNumber savedPlateNumber = plateNumberRepository.save(plateNumber);
        return mapToPlateNumberResponse(savedPlateNumber);
    }

    public List<PlateNumberResponse> getOwnerPlateNumbers(Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("Owner not found"));
        return plateNumberRepository.findByOwner(owner).stream().map(this::mapToPlateNumberResponse).toList();
    }

    private OwnerResponse mapToOwnerResponse(Owner owner) {
        return new OwnerResponse(
                owner.getId(),
                owner.getNames(),
                owner.getNationalId(),
                owner.getPhone(),
                owner.getAddress()
        );
    }

    private PlateNumberResponse mapToPlateNumberResponse(PlateNumber plateNumber) {
        return new PlateNumberResponse(
                plateNumber.getId(),
                plateNumber.getPlateNumber(),
                plateNumber.getIssuedDate(),
                plateNumber.getStatus()
        );
    }
}
