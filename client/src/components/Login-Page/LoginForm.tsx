import React, { useState } from "react"
import { LoginFormField } from "./LoginFormField"
import { LoginError } from "../../interfaces/LoginError"
import { LoadingIcon } from "../Global/LoadingIcon"

interface LoginFormProps {
    handleLoginField: (inputValue: string, inputFieldName: string) => void
    handleLoginButton: () => void
    loading: boolean
    loginError: LoginError
}

export const LoginForm: React.FC<LoginFormProps> = ({handleLoginField, handleLoginButton, loading, loginError}) => {
    
    return (
        <div className="login-form-container">
            <h2 className="login-form-title">
                Sign In
            </h2>
            {['Username', 'Password'].map(field =>
                <LoginFormField 
                    key={field}
                    placeholder={field} 
                    handleLoginField={handleLoginField} 
                    error={ loginError[`${field.toLowerCase()}Error` as keyof LoginError] } />
            )}
            
            <div className="registration-button-container">
                {
                    loading ? 
                        <div className="logging-in-loading">
                            <LoadingIcon /> 
                        </div>
                    :
                        <button className="login-button" onClick={handleLoginButton}>
                            Login
                        </button>
                }
            </div>

        </div>
    )
}