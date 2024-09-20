package com.example.booktracker.otp;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpVerification, Integer> {

    @Query("SELECT o FROM OtpVerification o WHERE o.username = :username and o.used = false and o.expirationTime > CURRENT_TIMESTAMP")
    Optional<OtpVerification> findActiveOtpByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM OtpVerification o WHERE o.username = :username")
    void deleteOtpByUsername(@Param("username") String username);
}
