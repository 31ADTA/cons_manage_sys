package com.construction.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Health check response payload")
public record HealthResponse(
        @Schema(description = "Overall service status", example = "UP")
        String status,

        @Schema(description = "Service name", example = "construction-management-backend")
        String service,

        @Schema(description = "Timestamp of health check", example = "2026-11-22T08:00:00Z")
        Instant timestamp,

        @Schema(description = "Database connectivity status", example = "UP")
        String database,

        @Schema(description = "Active environment profile", example = "local")
        String environment
) {}
