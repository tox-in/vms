package com.vms.repository;

import com.vms.model.Owner;
import com.vms.model.PlateNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlateNumberRepository extends JpaRepository<PlateNumber, Long> {
    List<PlateNumber> findByOwner(Owner owner);
    Optional<PlateNumber> findByPlateNumber(String plateNumber);
    List<PlateNumber> findByOwnerAndStatus(Owner owner, String status);

    boolean existsByPlateNumber(String plateNumber);
}
