package com.vms.dto.response;

public record OwnerResponse(
        Long id,
        String names,
        String nationalId,
        String phone,
        String address
) {
}
