package com.vms.dto.response;

public record PlateNumberResponse(
        Long id,
        String plateNumber,
        String issuedDate,
        String status
) {
}
