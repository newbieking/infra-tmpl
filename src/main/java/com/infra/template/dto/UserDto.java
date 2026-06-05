package com.infra.template.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record UserDto(
        Long id,
        @NotBlank String username,
        @NotBlank @Email String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
