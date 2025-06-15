package com.example.booktracker.maintenance;

import com.example.booktracker.otp.dto.OtpVerificationDTO;
import com.example.booktracker.otp.model.OtpVerification;
import com.example.booktracker.otp.service.OtpService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OtpCleanUpService {

    private final OtpService otpService;

    @Autowired
    public OtpCleanUpService(OtpService otpService) {
        this.otpService = otpService;
    }

    /**
     * Removes expired OTP (One-Time Password) verifications from the database.
     *
     * This method is scheduled to run at a fixed rate of 30 minutes (1800000 milliseconds).
     * It retrieves all expired OTP records from the repository and deletes them.
     *
     *
     * If an error occurs during the database access while trying to remove the expired OTPs,
     * a {@link RuntimeException} is thrown with an appropriate message.
     *
     *
     * @throws RuntimeException if there is a failure in removing expired OTPs due to data access issues.
     */
    @Transactional
    @Scheduled(fixedRate = 1800000)
    public void removeExpiredOtps() {
        try {
            List<OtpVerificationDTO> expiredOtps = otpService.getExpiredOtps();
            otpService.deleteOtps(expiredOtps);
        }
        catch (DataAccessException exception) {
            throw new RuntimeException("Failed to remove expired otps.");
        }
    }
}
