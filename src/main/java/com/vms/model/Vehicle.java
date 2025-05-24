package com.vms.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chassis_number", nullable = false, unique = true)
    private String chassisNumber;

    @Column(name = "manufacture_company", nullable = false)
    private String manufactureCompany;

    @Column(name = "manufacture_year", nullable = false)
    private Integer manufactureYear;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name = "current_price", nullable = false)
    private Double currentPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_owner_id", nullable = false)
    private Owner currentOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_plate_number_id", nullable = false)
    private PlateNumber currentPlateNumber;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "created_aat")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //default constructor
    public Vehicle() {}

    //parameterized constructor
    public Vehicle(String chassisNumber, String manufactureCompany, Integer manufactureYear, String modelName, Double currentPrice, Owner currentOwner, PlateNumber currentPlateNumber, LocalDateTime registrationDate) {
        this.chassisNumber = chassisNumber;
        this.manufactureCompany = manufactureCompany;
        this.manufactureYear = manufactureYear;
        this.modelName = modelName;
        this.currentPrice = currentPrice;
        this.currentOwner = currentOwner;
        this.currentPlateNumber = currentPlateNumber;
        this.registrationDate = registrationDate != null ? registrationDate : LocalDateTime.now();
    }

    //JPA lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if(registrationDate == null) {
            registrationDate = LocalDateTime.now();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getManufactureCompany() {
        return manufactureCompany;
    }

    public void setManufactureCompany(String manufactureCompany) {
        this.manufactureCompany = manufactureCompany;
    }

    public Integer getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(Integer manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Owner getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner(Owner currentOwner) {
        this.currentOwner = currentOwner;
    }

    public PlateNumber getCurrentPlateNumber() {
        return currentPlateNumber;
    }

    public void setCurrentPlateNumber(PlateNumber currentPlateNumber) {
        this.currentPlateNumber = currentPlateNumber;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", chassisNumber='" + chassisNumber + '\'' +
                ", manufactureCompany='" + manufactureCompany + '\'' +
                ", manufactureYear=" + manufactureYear +
                ", modelName='" + modelName + '\'' +
                ", currentPrice=" + currentPrice +
                ", currentOwner=" + currentOwner +
                ", currentPlateNumber=" + currentPlateNumber +
                ", registrationDate=" + registrationDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vehicle vehicle = (Vehicle) obj;
        return id != null && id.equals(vehicle.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
