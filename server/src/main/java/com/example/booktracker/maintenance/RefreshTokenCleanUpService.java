package com.example.booktracker.maintenance;

import com.example.booktracker.refresh_token.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public class RefreshTokenCleanUpService {

    private final RefreshTokenService refreshTokenService;

    public RefreshTokenCleanUpService(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @Scheduled(cron = "0 30 3 * * *") // 3:30 AM daily
    @Transactional
    public void cleanExpiredOrUsedTokens() {
        List<Long> expiredTokenIds = refreshTokenService.findExpiredOrUsedTokenIds();

        for (Long tokenId : expiredTokenIds) {
            refreshTokenService.deleteById(tokenId);
        }
    }

}
