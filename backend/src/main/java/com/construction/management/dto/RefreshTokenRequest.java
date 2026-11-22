package com.construction.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Refresh token request payload")
public record RefreshTokenRequest(
        @Schema(description = "Valid refresh token string", example = "eyJhbGciOiJIUzI1NiIsIn...")
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}
