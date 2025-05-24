package com.vms.dto.response;

import java.time.LocalDateTime;

public record OwnershipHistoryResponse(
        String previousOwner,
        String newOwner,
        String previousPlateNumber,
        String newPlateNumber,
        Double transferPrice,
        LocalDateTime transferDate
) {
}
