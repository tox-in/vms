package com.vms.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "plate_numbers")
public class PlateNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plate_number", nullable = false, unique = true)
    private String plateNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "issued_date", nullable = false)
    private LocalDateTime issuedDate;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //Default constructor
    public PlateNumber() {}

    //parameterized constructor
    public PlateNumber(Long id, String plateNumber, Owner owner, LocalDateTime issuedDate, String status) {
        this.plateNumber = plateNumber;
        this.owner = owner;
        this.issuedDate = issuedDate;
        this.status = "AVAILABLE";
    }

    //status based constructor
    public PlateNumber(String plateNumber, Owner owner, LocalDateTime issuedDate, String status) {
        this.plateNumber = plateNumber;
        this.owner = owner;
        this.issuedDate = issuedDate;
        this.status = status != null ? status : "AVAILABLE";
    }

    //JPA lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = "AVAILABLE";
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    //Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public LocalDateTime getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDateTime issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        return "PlateNumber{" +
                "id=" + id +
                ", plateNumber='" + plateNumber + '\'' +
                ", owner=" + owner +
                ", issuedDate=" + issuedDate +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PlateNumber that = (PlateNumber) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
