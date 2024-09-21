import React from "react";

interface LoginFormFieldProps {
    placeholder: string
    handleLoginField: (inputValue: string, inputFieldName: string) => void 
    handleLoginButton: () => void
    handleKeyDown: (event: React.KeyboardEvent<HTMLInputElement>, functionToCall: () => void) => void
    error: string
}


export const LoginFormField: React.FC<LoginFormFieldProps> = ({placeholder, handleLoginField, handleLoginButton, handleKeyDown, error}) => {
    const isPasswordField: boolean = placeholder.toLowerCase() === 'password';

    return (
        <div className="login-form-field-container">
            <input type={isPasswordField ? 'password' : 'text'} className="login-field-input" placeholder={placeholder} onChange={(inputValue) => handleLoginField(placeholder,inputValue.target.value)} onKeyDown={(event) => handleKeyDown(event, handleLoginButton)} />
            {
                isPasswordField ?
                    <div className="forgot-password-and-error-container">
                        {
                            error.length > 0 && <p className="password-required-error">{error}</p>
                        }                    
                        <p className="forgot-password">Forgot password?</p>    
                    </div>
                :
                error.length > 0 &&
                <div className="login-error">
                    <p>{error}</p>
                </div>
        }
        </div>
    )
}