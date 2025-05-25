package com.vms.dto.response;

import java.time.LocalDateTime;

public record PlateNumberResponse(
        Long id,
        String plateNumber,
        LocalDateTime issuedDate,
        String status
) {
}
