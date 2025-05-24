package com.vms.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record PlateNumberRequest(
        @NotBlank(message = "Plate number is required")
        String plateNumber,

        LocalDateTime issuedDate
) {
}
