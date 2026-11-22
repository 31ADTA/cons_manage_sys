package com.construction.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User login request payload")
public record LoginRequest(
        @Schema(description = "User email address", example = "owner@construction-mgmt.local")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "User password", example = "Owner@123456")
        @NotBlank(message = "Password is required")
        String password
) {}
