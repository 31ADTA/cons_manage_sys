package com.construction.management.service;

import com.construction.management.dto.AuthResponse;
import com.construction.management.dto.LoginRequest;
import com.construction.management.dto.RefreshTokenRequest;
import com.construction.management.entity.RefreshToken;
import com.construction.management.entity.User;
import com.construction.management.repository.RefreshTokenRepository;
import com.construction.management.repository.UserRepository;
import com.construction.management.security.CustomUserDetails;
import com.construction.management.security.JwtProperties;
import com.construction.management.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       JwtProperties jwtProperties,
                       UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        String accessToken = jwtService.generateAccessToken(
                Map.of("role", user.getRole().name(), "userId", user.getId()),
                userDetails
        );

        String refreshTokenStr = jwtService.generateRefreshToken(userDetails);
        Instant expiryDate = Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs());

        RefreshToken refreshToken = new RefreshToken(user, refreshTokenStr, expiryDate);
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                accessToken,
                refreshTokenStr,
                "Bearer",
                jwtProperties.getAccessTokenExpirationMs(),
                user.getEmail(),
                user.getRole().name(),
                user.getName()
        );
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (tokenEntity.isRevoked() || tokenEntity.isExpired()) {
            refreshTokenRepository.delete(tokenEntity);
            throw new BadCredentialsException("Refresh token is expired or revoked");
        }

        User user = tokenEntity.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        if (!jwtService.isTokenValid(tokenEntity.getToken(), userDetails)) {
            refreshTokenRepository.delete(tokenEntity);
            throw new BadCredentialsException("Invalid refresh token signature");
        }

        String newAccessToken = jwtService.generateAccessToken(
                Map.of("role", user.getRole().name(), "userId", user.getId()),
                userDetails
        );

        String newRefreshTokenStr = jwtService.generateRefreshToken(userDetails);
        tokenEntity.setToken(newRefreshTokenStr);
        tokenEntity.setExpiryDate(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs()));
        refreshTokenRepository.save(tokenEntity);

        return new AuthResponse(
                newAccessToken,
                newRefreshTokenStr,
                "Bearer",
                jwtProperties.getAccessTokenExpirationMs(),
                user.getEmail(),
                user.getRole().name(),
                user.getName()
        );
    }
}
