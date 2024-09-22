import React, { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import '../../styles/verification-page.css';
import { LoadingIcon } from "../../components/Global/LoadingIcon";
import { registerUser, requestOTP, verifyOtp } from "../../services/userAccount";
import { handleKeyDown } from "../../utils/handleKeyDown";


interface customError {
    navigationError: string
    otpRequestError: string
    otpInputError: string
    otpVerificationError: string
}

interface customLoading {
    page: boolean
    otpSubmit: boolean
    resend: boolean
}

export const VerificationPage: React.FC = () => {
    const navigate = useNavigate();

    const location = useLocation();
    const email: string | null = location.state?.email;
    const username: string | null = location.state?.username;
    const password: string | null = location.state?.password;

    // states
    const [loading, setLoading] = useState<customLoading>({
        page: false,
        otpSubmit: false,
        resend: true
    });
    const [error, setError] = useState<customError>({
        navigationError: '',
        otpRequestError: '',
        otpInputError: '',
        otpVerificationError: ''
    });
    const [otp, setOtp] = useState<string>('');
    const [otpReSent, setOtpReSent] = useState<boolean>(false);
    const [userVerified, setUserVerified] = useState<boolean>(false);

    // request otp
    useEffect(() => {

        if (!email || !username) {
            setError(prev => ({
                ...prev,
                navigationError: "Invalid navigation."
            }));
            return;
        }

        requestForOtp(email, false);
    }, [email, username]); 

    useEffect(() => {
        if (email && username && password && userVerified) {
            requestUserRegistration(email, username, password);
        }
        return () => {
            setUserVerified(false);
        }
    }, [userVerified])

    const requestUserRegistration = async (email: string, username: string, password: string) => {
        try {
            await registerUser(email, username, password);
            navigate("/user/login", {state: {userLoginDetails: {username, password}, "registeredMessage": "You have successfully been registered. You can now login."}});
        }
        catch (error: any) {
            navigate('/user/registration', {state: {alreadyTypedDetails: {email, username, password}, errorRegisteringAfterVerification: error.message}});
        }
    }

    const requestForOtp = async (email: string, resend: boolean) => {
        try {
            resend ? 
            setLoading(prev => ({
                ...prev,
                resend: true
            })) :
            setLoading(prev => ({
                ...prev,
                page: true
            }));

            await requestOTP(email, resend);
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
                page: false,
                resend: false
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
            email && await verifyOtp(email, otp);
            setUserVerified(true);
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

    const handleResendButton = async () => {
        email && username && await requestForOtp(email, true);
        setOtpReSent(true);
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
                            <input className="otp-input" placeholder="Enter OTP" onChange={(inputValue) => handleOtpInput(inputValue.target.value)} onKeyDown={(event) => handleKeyDown(event, handleSubmitOtp)}  />
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
                        {
                            loading.resend ? 
                            <div className="otp-resending-loading">
                                <LoadingIcon />
                            </div>
                            :
                            otpReSent ?
                            <p>Otp Resent.</p>
                            :
                            <button className="resend-otp" onClick={handleResendButton}>Resend OTP</button>
                        }
                    </div>
                </div>
            }
        </>
    );
}