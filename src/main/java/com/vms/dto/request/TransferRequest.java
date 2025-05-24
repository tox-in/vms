package com.vms.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferRequest (
        @NotNull(message = "New owner ID is required")
        Long newOwnerId,

        @NotNull(message = "New plate number ID is required")
        Long newPlateNumberId,

        @NotNull(message = "Transfer price is required")
        @Positive(message = "Transfer price must be positive")
        Double transferPrice
) {
}
