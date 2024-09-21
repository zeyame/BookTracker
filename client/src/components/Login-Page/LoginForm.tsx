import React, { useState } from "react"
import { LoginFormField } from "./LoginFormField"
import { LoginError } from "../../interfaces/LoginError"
import { LoadingIcon } from "../Global/LoadingIcon"

interface LoginFormProps {
    handleLoginField: (inputValue: string, inputFieldName: string) => void
    handleLoginButton: () => void
    handleSignUpButton: () => void
    handleKeyDown: (event: React.KeyboardEvent<HTMLInputElement>, functionToCall: () => void) => void,
    loading: boolean
    loginError: LoginError
}

export const LoginForm: React.FC<LoginFormProps> = ({handleLoginField, handleLoginButton, handleSignUpButton, handleKeyDown, loading, loginError}) => {

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
                    error={ loginError[`${field.toLowerCase()}Error` as keyof LoginError] } 
                    handleLoginButton={handleLoginButton}
                    handleKeyDown={handleKeyDown}
                    />
            )}
            
            <div className="login-button-container">
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

            <div className="new-to-shelf-quest-container">
                <hr className="divider" />
                <span className="divider-text">New to Shelf Quest?</span>
                <hr className="divider" />
            </div>
            
            <button className="login-page-sign-up-button" onClick={handleSignUpButton}>
                Sign Up
            </button>

        </div>
    )
}