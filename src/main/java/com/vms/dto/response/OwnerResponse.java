package com.vms.dto.response;

public record OwnerResponse(
        Long id,
        String names,
        String email,
        String nationalId,
        String phone,
        String address
) {
}
