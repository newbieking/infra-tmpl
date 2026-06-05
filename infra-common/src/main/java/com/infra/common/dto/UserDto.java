package com.infra.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDto(
        Long id,
        @NotBlank String username,
        @Email String email,
        String createdAt,
        String updatedAt
) {
}
