package com.example.booktracker.refresh_token.repository;

import com.example.booktracker.refresh_token.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenAndUserId(String token, Long userId);

    @Query(value = """
            SELECT id FROM refresh_tokens 
            WHERE expires_at < :now OR used = true    
     """, nativeQuery = true)
    List<Long> findExpiredOrUsedTokenIds(@Param("now") LocalDateTime now);

    void deleteByUserId(Long userId);
}
