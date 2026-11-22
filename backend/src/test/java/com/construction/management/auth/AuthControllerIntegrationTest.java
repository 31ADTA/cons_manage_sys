package com.construction.management.auth;

import com.construction.management.dto.LoginRequest;
import com.construction.management.dto.RefreshTokenRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("construction_mgmt_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("app.owner.email", () -> "owner@construction-mgmt.local");
        registry.add("app.owner.password", () -> "Owner@123456");
        registry.add("app.owner.name", () -> "System Owner");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestEndpointConfig {
        @RestController
        @RequestMapping("/api/test")
        static class TestProtectedController {
            @GetMapping("/protected")
            public ResponseEntity<String> getProtected() {
                return ResponseEntity.ok("ACCESS_GRANTED");
            }
        }
    }

    @Test
    @DisplayName("POST /api/auth/login - Success with seeded Owner credentials")
    void shouldLoginSuccessfullyWithSeededOwnerCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("owner@construction-mgmt.local", "Owner@123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value("owner@construction-mgmt.local"))
                .andExpect(jsonPath("$.role").value("OWNER"))
                .andExpect(jsonPath("$.name").value("System Owner"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Fails with 401 on wrong password")
    void shouldFailLoginWithIncorrectPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("owner@construction-mgmt.local", "WrongPassword123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/refresh - Successfully refreshes access token")
    void shouldRefreshAccessTokenWithValidRefreshToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("owner@construction-mgmt.local", "Owner@123456");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String refreshToken = jsonNode.get("refreshToken").asText();

        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("OWNER"));
    }

    @Test
    @DisplayName("POST /api/auth/refresh - Fails with 401 on forged/invalid refresh token")
    void shouldFailRefreshWithInvalidRefreshToken() throws Exception {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("invalid.token.string");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Protected endpoints reject unauthenticated requests with 401")
    void shouldDenyAccessToProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/test/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Protected endpoints accept requests with valid JWT Bearer token")
    void shouldAllowAccessToProtectedEndpointWithValidJwtToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("owner@construction-mgmt.local", "Owner@123456");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = jsonNode.get("accessToken").asText();

        mockMvc.perform(get("/api/test/protected")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }
}
