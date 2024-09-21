import React from "react";
import { RegistrationFormField } from "./RegistrationFormField";
import { Link } from "react-router-dom";
import { LoadingIcon } from "../Global/LoadingIcon";
import { RegistrationError } from "../../interfaces/RegistrationError";

interface RegistrationFormProps {
    handleUserRegistration: (inputFieldName: string, inputValue: string) => void
    handleCreateAccount: () => void
    handleKeyDown: (event: React.KeyboardEvent<HTMLInputElement>, functionToCall: () => void) => void,
    loading: boolean
    registrationError: RegistrationError
}


export const RegistrationForm: React.FC<RegistrationFormProps> = ({handleUserRegistration, handleCreateAccount, handleKeyDown, loading, registrationError}) => {
    return (
        <div className="registration-form-container">
            <div>
                <h1 className="registration-form-title">Create Account</h1>
            </div>

            {['Email', 'Username', 'Password', 'Confirm Password'].map(field =>
                <RegistrationFormField 
                    key={field}
                    placeholder={field} 
                    handleInputField={handleUserRegistration} 
                    error={
                        field === 'Confirm Password' ? registrationError.otherError 
                            : 
                        registrationError[`${field.toLowerCase()}Error` as keyof RegistrationError]}
                    handleCreateAccount={handleCreateAccount}
                    handleKeyDown={handleKeyDown}
                        />
            )}

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