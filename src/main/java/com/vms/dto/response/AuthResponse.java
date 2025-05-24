package com.vms.dto.response;

public record AuthResponse(
        String token,
        String email,
        String role
) {
}
