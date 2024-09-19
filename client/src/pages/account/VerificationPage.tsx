import React, { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import '../../styles/verification-page.css';
import { LoadingIcon } from "../../components/Global/LoadingIcon";
import { requestOTP } from "../../services/userAccount";


interface customError {
    navigationError: boolean
    otpRequestError: boolean
}

export const VerificationPage: React.FC = () => {

    const location = useLocation();
    const email: string | null = location.state?.email;
    const username: string | null = location.state?.username;

    console.log("Email: ", email);
    console.log("Username: ", username);
    
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<customError>({
        navigationError: false,
        otpRequestError: false
    });

    // request otp
    useEffect(() => {

        if (!email || !username) {
            setError(prev => ({
                ...prev,
                navigationError: true
            }));
            return;
        }

        requestForOtp(email, username);
    }, [email, username]); 

    const requestForOtp = async (email: string, username: string) => {
        try {
            setLoading(true);
            await requestOTP(email, username);
        }
        catch (error: any) {
            setError(prev => ({
                ...prev,
                otpRequestError: true
            }));
        }
        finally {
            setLoading(false);
        }
    }

    return (
        <>
            {
                loading ? 
                <div className="verification-page-container">
                    <div className="verification-page-loading">
                        <p className="verification-loading-message">Loading</p>
                        <LoadingIcon width="40" height="30" />
                    </div> 
                </div>
                    :
                error.navigationError ?
                <div className="verification-page-error">
                    Invalid navigation. Please return to the registration page or <Link className="return-home-from-verification" to={'/'}>click here</Link> to return to the home page.
                </div>
                    :
                error.otpRequestError ?
                <div className="verification-page-error">
                    An error occurred while sending the OTP for verification. Please refresh to try again or return to the registration page. 
                </div>
                    :
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
                        <button className="resend-otp">Resend OTP</button>
                    </div>
                </div>
            }
        </>
    );
}