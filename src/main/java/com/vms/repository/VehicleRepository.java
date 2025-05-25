package com.vms.repository;

import com.vms.model.Owner;
import com.vms.model.PlateNumber;
import com.vms.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByChassisNumber(String chassisNumber);
    Optional<Vehicle> findByCurrentPlateNumber(PlateNumber currentPlateNumber);
    List<Vehicle> findByCurrentOwner(Owner owner);

    boolean existsByChassisNumber(String chassisNumber);
}
