package com.vms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_ownership_history")
public class VehicleOwnershipHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @NotNull(message = "Vehicle is required")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_owner_id", nullable = false)
    @NotNull(message = "Previous owner is required")
    private Owner previousOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_owner_id", nullable = false)
    @NotNull(message = "New owner is required")
    private Owner newOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_plate_number_id")
    private PlateNumber previousPlateNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_plate_number_id", nullable = false)
    @NotNull(message = "New plate number is required")
    private PlateNumber newPlateNumber;

    @Column(name = "transfer_price", nullable = false)
    @NotNull(message = "Transfer price is required")
    @Positive(message = "Transfer price must be positive")
    private Double transferPrice;

    @Column(name = "transfer_date", nullable = false)
    @NotNull(message = "Transfer date is required")
    private LocalDateTime transferDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transferred_by", nullable = false)
    @NotNull(message = "User who processed the transfer is required")
    private User transferredBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    //default constructor
    public VehicleOwnershipHistory() {}


    //parameterized constructor
    public VehicleOwnershipHistory(Vehicle vehicle, Owner previousOwner, Owner newOwner, PlateNumber newPlateNumber, Double transferPrice, LocalDateTime transferDate, User transferredBy) {
        this.vehicle = vehicle;
        this.previousOwner = previousOwner;
        this.newOwner = newOwner;
        this.newPlateNumber = newPlateNumber;
        this.transferPrice = transferPrice;
        this.transferDate = transferDate != null ? transferDate : LocalDateTime.now();
        this.transferredBy = transferredBy;
    }

    //JPA lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if (transferDate == null) {
            transferDate = LocalDateTime.now();
        }
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Owner getPreviousOwner() {
        return previousOwner;
    }

    public void setPreviousOwner(Owner previousOwner) {
        this.previousOwner = previousOwner;
    }

    public Owner getNewOwner() {
        return newOwner;
    }

    public void setNewOwner(Owner newOwner) {
        this.newOwner = newOwner;
    }

    public PlateNumber getPreviousPlateNumber() {
        return previousPlateNumber;
    }

    public void setPreviousPlateNumber(PlateNumber previousPlateNumber) {
        this.previousPlateNumber = previousPlateNumber;
    }

    public PlateNumber getNewPlateNumber() {
        return newPlateNumber;
    }

    public void setNewPlateNumber(PlateNumber newPlateNumber) {
        this.newPlateNumber = newPlateNumber;
    }

    public Double getTransferPrice() {
        return transferPrice;
    }

    public void setTransferPrice(Double transferPrice) {
        this.transferPrice = transferPrice;
    }

    public LocalDateTime getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDateTime transferDate) {
        this.transferDate = transferDate;
    }

    public User getTransferredBy() {
        return transferredBy;
    }

    public void setTransferredBy(User transferredBy) {
        this.transferredBy = transferredBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    //Business logic methods
    public boolean isOwnershipChange() {
        return !previousOwner.equals(newOwner);
    }

    public boolean isPlateNumberChange() {
        if(previousPlateNumber == null) {
            return true; //first registration
        }
        return !previousPlateNumber.equals(newPlateNumber);
    }

    public String getTransferType() {
        boolean ownerChanged = isOwnershipChange();
        boolean plateChanged = isPlateNumberChange();

        if(ownerChanged && plateChanged) {
            return "OWNERSHIP_AND_PLATE_TRANSFER";
        } else if(ownerChanged) {
            return "OWNERSHIP_TRANSFER";
        } else if (plateChanged) {
            return "PLATE_NUMBER_CHANGE";
        } else {
            return "RECORD_UPDATE";
        }
    }

    //toString (lazy loading safe)

    @Override
    public String toString() {
        return "VehicleOwnershipHistory{" +
                "id=" + id +
                ", vehicle=" + vehicle +
                ", previousOwner=" + previousOwner +
                ", newOwner=" + newOwner +
                ", previousPlateNumber=" + previousPlateNumber +
                ", newPlateNumber=" + newPlateNumber +
                ", transferPrice=" + transferPrice +
                ", transferDate=" + transferDate +
                ", transferredBy=" + transferredBy +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VehicleOwnershipHistory that = (VehicleOwnershipHistory) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
