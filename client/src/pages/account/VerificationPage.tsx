import React from "react";
import { useLocation } from "react-router-dom";
import '../../styles/verification-page.css';

export const VerificationPage: React.FC = () => {

    const location = useLocation();
    const email: string | null = location.state.email;

    return (
        <div className="verification-page-container">
            <h1 className="verification-page-app-name">Shelf Quest</h1>
            <div className="verification-container">
                <h2 className="verification-page-title">Verify email address</h2> 
                <p className="verification-message">To verify your email , we've sent a One Time Password (OTP) to {email}</p>
                <div className="otp-input-container">
                    <input className="otp-input" placeholder="Enter OTP"  />
                </div>
                <button className="submit-otp-button">
                    Create account
                </button>
                <p className="resend-otp">Resend OTP</p>
            </div>
        </div>
    );
}