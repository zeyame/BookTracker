package com.example.booktracker.otp;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpVerification, Integer> {

    /**
     * Finds an active OTP (One-Time Password) by email.
     * <p>
     * This query searches for an OTP associated with the specified email that:
     * <ul>
     *     <li>Has not been used (i.e., {@code used = false})</li>
     *     <li>Has an expiration time that is still valid (i.e., {@code expirationTime > CURRENT_TIMESTAMP})</li>
     * </ul>
     *
     * @param email the email associated with the OTP
     * @return an {@link Optional} containing the active {@link OtpVerification} if found, or an empty {@link Optional} if no such OTP exists
     */
    @Query("SELECT o FROM OtpVerification o WHERE o.email = :email and o.used = false and o.expirationTime > CURRENT_TIMESTAMP")
    Optional<OtpVerification> findActiveOtpByEmail(@Param("email") String email);


    /**
     * Deletes all OTPs associated with the given email.
     * <p>
     * This query deletes all records of OTPs linked to the provided email, regardless of their status (used, unused, expired).
     * <p>
     * The operation is performed in a transactional context to ensure data integrity.
     *
     * @param email the email for which all OTP records should be deleted
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM OtpVerification o WHERE o.email = :email")
    void deleteOtpByEmail(@Param("email") String email);


    /**
     * Finds all expired OTPs (One-Time Passwords).
     * <p>
     * This query retrieves all OTPs where the expiration time has already passed (i.e., {@code expirationTime < CURRENT_TIMESTAMP}).
     *
     * @return a {@link List} of {@link OtpVerification} objects that have expired
     */
    @Query("SELECT o FROM OtpVerification o WHERE o.expirationTime < CURRENT_TIMESTAMP ")
    List<OtpVerification> findExpiredOtps();
}
