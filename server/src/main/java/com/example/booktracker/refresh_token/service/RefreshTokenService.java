package com.example.booktracker.refresh_token.service;

import com.example.booktracker.extra_services.JwtService;
import com.example.booktracker.refresh_token.model.RefreshToken;
import com.example.booktracker.refresh_token.repository.RefreshTokenRepository;
import com.example.booktracker.user.exception.InvalidCredentialsException;
import com.example.booktracker.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class RefreshTokenService {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshTokenTTL;

    public RefreshTokenService(JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public String createRefreshToken(Long userId, String username) {
        // Delete any existing refresh tokens for this user
        refreshTokenRepository.deleteByUserId(userId);

        // Generate a new refresh token string using the JwtService
        String token = jwtService.generateRefreshToken(username);

        saveRefreshToken(userId, token);

        return token;
    }

    @Transactional
    public Map<String, String> refreshAccessToken(String oldRefreshToken, Long userId) {
        String username = jwtService.extractUsername(oldRefreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenAndUserId(oldRefreshToken, userId)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid or unknown refresh token."));

        if (storedToken.isUsed() || storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidCredentialsException("Refresh token is expired or already used.");
        }

        // Invalidate old token
        storedToken.setUsed(true);
        refreshTokenRepository.save(storedToken);

        // create and save new refresh token
        String newRefreshToken = jwtService.generateRefreshToken(username);
        saveRefreshToken(userId, newRefreshToken);

        String newJwt = jwtService.generateToken(username);

        return Map.of(
                "token", newJwt,
                "refreshToken", newRefreshToken
        );
    }

    public List<Long> findExpiredOrUsedTokenIds() {
        LocalDateTime now = LocalDateTime.now();
        return refreshTokenRepository.findExpiredOrUsedTokenIds(now);
    }

    @Transactional
    public void deleteById(Long id) {
        refreshTokenRepository.deleteById(id);
    }



    private void saveRefreshToken(Long userId, String token) {
        // create and save the new token entity
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(token);
        refreshToken.setIssuedAt(LocalDateTime.now());
        refreshToken.setExpiresAt(LocalDateTime.now().plus(refreshTokenTTL, ChronoUnit.MILLIS));
        refreshToken.setUsed(false);

        refreshTokenRepository.save(refreshToken);

    }


}
