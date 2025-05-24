package com.vms.repository;

import com.vms.model.Vehicle;
import com.vms.model.VehicleOwnershipHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface VehicleOwnershipHistoryRepository extends JpaRepository<VehicleOwnershipHistory, Long> {
    List<VehicleOwnershipHistory> findByVehicleOrderByTransferDateDesc(Vehicle vehicle);
    List<VehicleOwnershipHistory> findByVehicleIdOrderByTransferDateDesc(Long vehicleId);
    List<VehicleOwnershipHistory> findByVehicleOrderByTransferDateDesc(Vehicle vehicle, Pageable pageable);
}
