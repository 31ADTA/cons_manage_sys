package com.construction.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response payload containing tokens and user information")
public record AuthResponse(
        @Schema(description = "JWT Access Token for Bearer authentication")
        String accessToken,

        @Schema(description = "Refresh token for generating new access tokens")
        String refreshToken,

        @Schema(description = "Token type", example = "Bearer")
        String tokenType,

        @Schema(description = "Access token validity duration in milliseconds", example = "900000")
        long expiresIn,

        @Schema(description = "Authenticated user email", example = "owner@construction-mgmt.local")
        String email,

        @Schema(description = "Authenticated user role", example = "OWNER")
        String role,

        @Schema(description = "Authenticated user full name", example = "System Owner")
        String name
) {}
