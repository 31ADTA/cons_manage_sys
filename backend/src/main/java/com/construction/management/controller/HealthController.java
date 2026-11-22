package com.construction.management.controller;

import com.construction.management.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "System health check and diagnostic endpoints")
public class HealthController {

    private final DataSource dataSource;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    public HealthController(@Autowired(required = false) DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    @Operation(
            summary = "Get service health status",
            description = "Returns current operational status of the service, active profile, and database connectivity."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Service health details",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = HealthResponse.class))
    )
    public ResponseEntity<HealthResponse> getHealth() {
        String dbStatus = checkDatabaseStatus();

        HealthResponse response = new HealthResponse(
                "UP",
                "construction-management-backend",
                Instant.now(),
                dbStatus,
                activeProfile
        );

        return ResponseEntity.ok(response);
    }

    private String checkDatabaseStatus() {
        if (dataSource == null) {
            return "NOT_CONFIGURED";
        }
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2) ? "UP" : "DEGRADED";
        } catch (Exception e) {
            return "DOWN (" + e.getMessage() + ")";
        }
    }
}
