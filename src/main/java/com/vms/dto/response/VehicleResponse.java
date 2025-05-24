package com.vms.dto.response;

import java.time.LocalDateTime;

public record VehicleResponse(
        Long id,
        String chassisNumber,
        String manufactureCompany,
        Integer manufactureYear,
        String modelName,
        Double currentPrice,
        OwnerResponse currentOwner,
        PlateNumberResponse currentPlateNumber,
        LocalDateTime registrationDate
) {
}
