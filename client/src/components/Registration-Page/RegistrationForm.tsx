import React from "react";
import { RegistrationFormField } from "./RegistrationFormField";
import { Link } from "react-router-dom";
import { LoadingIcon } from "../Global/LoadingIcon";
import { RegistrationError } from "../../interfaces/RegistrationError";
import { UserRegistration } from "../../interfaces/UserRegistration";

interface RegistrationFormProps {
    userRegistration: UserRegistration
    handleUserRegistration: (inputFieldName: string, inputValue: string) => void
    handleCreateAccount: () => void
    handleKeyDown: (event: React.KeyboardEvent<HTMLInputElement>, functionToCall: () => void) => void,
    loading: boolean
    registrationError: RegistrationError
}



export const RegistrationForm: React.FC<RegistrationFormProps> = ({userRegistration, handleUserRegistration, handleCreateAccount, handleKeyDown, loading, registrationError}) => {
    
    const errorMap: Record<string, string> = {
        'Email': registrationError.emailError,
        'Username': registrationError.usernameError,
        'Password': registrationError.passwordError,
        'Confirm Password': registrationError.otherError,  // Or handle this appropriately
    };
    
    return (
        <div className="registration-form-container">
            <div>
                <h1 className="registration-form-title">Create Account</h1>
            </div>

            {['Email', 'Username', 'Password', 'Confirm Password'].map(field =>
                <RegistrationFormField 
                    key={field}
                    value={userRegistration[field.toLowerCase().split(' ').join('') as keyof UserRegistration] || ''}
                    placeholder={field} 
                    handleInputField={handleUserRegistration} 
                    error={errorMap[field] || ''}
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