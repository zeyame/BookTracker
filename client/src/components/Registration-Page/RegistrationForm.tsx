import React from "react";
import { RegistrationFormField } from "./RegistrationFormField";
import { Link } from "react-router-dom";
import { LoadingIcon } from "../Global/LoadingIcon";
import { RegistrationError } from "../../interfaces/RegistrationError";

interface RegistrationFormProps {
    handleUserRegistration: (inputFieldName: string, inputValue: string) => void
    handleCreateAccount: () => void
    loading: boolean
    registrationError: RegistrationError
}


export const RegistrationForm: React.FC<RegistrationFormProps> = ({handleUserRegistration, handleCreateAccount, loading, registrationError}) => {
    return (
        <div className="registration-form-container">
            <div>
                <h1 className="registration-form-title">Create Account</h1>
            </div>

            <RegistrationFormField placeholder="Email" handleInputField={handleUserRegistration} error={registrationError.emailError} />

            <RegistrationFormField placeholder="Username" handleInputField={handleUserRegistration} error={registrationError.usernameError} />

            <RegistrationFormField placeholder="Password" handleInputField={handleUserRegistration} error={registrationError.passwordError} />

            <RegistrationFormField placeholder="Confirm Password" handleInputField={handleUserRegistration} error={registrationError.otherError} />

            <div className="registration-button-container">
                {
                    loading ? 
                        <div className="registering-loading">
                            <LoadingIcon /> 
                        </div>
                    :
                        <button className="registration-button" onClick={handleCreateAccount}>
                            Create account
                        </button>
                }
            </div>
            
            <div className="already-have-account-message-div">
                <p className="already-have-account-message">
                    Already have an account? <Link className="already-have-account-sign-in-link" to={"/user/login"}>Sign in</Link>
                </p>
            </div>
        </div>
    );
}