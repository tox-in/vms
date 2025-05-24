package com.vms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VehicleRequest(
        @NotBlank(message = "Chassis number is required")
        String chassisNumber,

        @NotBlank(message = "Manufacture company is required")
        String manufactureCompany,

        @NotNull(message = "Manufacture year is required")
        @Positive(message = "Manufacture year must be positive")
        Integer manufactureYear,

        @NotBlank(message = "Model name is required")
        String modelName,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        Double price,

        @NotNull(message = "Owner ID is required")
        Long ownerId,

        @NotNull(message = "Plate number ID is required")
        Long plateNumberId
) {
}
