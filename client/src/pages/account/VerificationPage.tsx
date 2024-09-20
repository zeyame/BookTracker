import React, { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import '../../styles/verification-page.css';
import { LoadingIcon } from "../../components/Global/LoadingIcon";
import { requestOTP, verifyOtp } from "../../services/userAccount";


interface customError {
    navigationError: string
    otpRequestError: string
    otpInputError: string
    otpVerificationError: string
}

interface customLoading {
    page: boolean
    otpSubmit: boolean
}

export const VerificationPage: React.FC = () => {
    const navigate = useNavigate();

    const location = useLocation();
    const email: string | null = location.state?.email;
    const username: string | null = location.state?.username;
    
    // states
    const [loading, setLoading] = useState<customLoading>({
        page: false,
        otpSubmit: false
    });
    const [error, setError] = useState<customError>({
        navigationError: '',
        otpRequestError: '',
        otpInputError: '',
        otpVerificationError: ''
    });
    const [otp, setOtp] = useState<string>('');

    // request otp
    useEffect(() => {

        if (!email || !username) {
            setError(prev => ({
                ...prev,
                navigationError: "Invalid navigation."
            }));
            return;
        }

        requestForOtp(email, username);
    }, [email, username]); 

    const requestForOtp = async (email: string, username: string) => {
        try {
            setLoading(prev => ({
                ...prev,
                page: true
            }));
            await requestOTP(email, username);
        }
        catch (error: any) {
            setError(prev => ({
                ...prev,
                otpRequestError: error.message
            }));
        }
        finally {
            setLoading(prev => ({
                ...prev,
                page: false
            }));
        }
    }

    const handleOtpInput = (otpValue: string) => {
        setOtp(otpValue);
    }

    const handleSubmitOtp = async () => {
        setError(prev => ({
            ...prev,
            otpInputError: ''
        }));

        if (otp.length < 1) {
            setError(prev => ({
                ...prev,
                otpInputError: "OTP is required."
            }));
            return;
        }

        try {
            setLoading(prev => ({
                ...prev,
                otpSubmit: true
            }))
            username && await verifyOtp(username, otp);
            navigate("/user/login");
        }
        catch (error: any) {
            setError(prev => ({
                ...prev,
                otpVerificationError: error.message
            }));
        }
        finally {
            setLoading(prev => ({
                ...prev, 
                otpSubmit: false
            }));
        }
    }

    return (
        <>
            {
                loading.page ? 
                <div className="verification-page-container">
                    <div className="verification-page-loading">
                        <p className="verification-loading-message">Loading</p>
                        <LoadingIcon width="40" height="30" />
                    </div> 
                </div>
                    :
                error.navigationError.length > 0 ? 
                <div className="verification-page-error">
                    {error.navigationError} Please return to the registration page or <Link className="return-home-from-verification" to={'/'}>click here</Link> to return to the home page.
                </div>
                    :
                error.otpRequestError.length > 0 ?
                <div className="verification-page-error">
                    {error.otpRequestError}
                </div>
                    :
                <div className="verification-page-container">
                    <h1 className="verification-page-app-name">Shelf Quest</h1>
                    <div className="verification-container">
                        <h2 className="verification-page-title">Verify email address</h2> 
                        <p className="verification-message">To verify your email , we've sent a One Time Password (OTP) to {email}</p>
                        <div className="otp-input-container">
                            <input className="otp-input" placeholder="Enter OTP" onChange={(inputValue) => handleOtpInput(inputValue.target.value)}  />
                            {
                                error.otpInputError.length > 0
                                    ?
                                <p className="empty-otp-error">{error.otpInputError}</p>
                                    :
                                error.otpVerificationError.length > 0 ?
                                <p className="otp-verification-error">{error.otpVerificationError}</p>
                                    :
                                <p></p>
                            }
                        </div>
                        {
                            loading.otpSubmit ? 
                            <div className="otp-submit-button-loading">
                                <LoadingIcon />
                            </div>
                            :
                            <button className="submit-otp-button" onClick={() => handleSubmitOtp()}>
                                Create account
                            </button>
                        }
                        <button className="resend-otp">Resend OTP</button>
                    </div>
                </div>
            }
        </>
    );
}