import React from "react";
import { RegistrationFormField } from "./RegistrationFormField";
import { Link } from "react-router-dom";

export const RegistrationForm: React.FC = () => {
    return (
        <div className="registration-form-container">
            <div>
                <h1 className="registration-form-title">Create Account</h1>
            </div>
            <RegistrationFormField placeholder="Email" />
            <RegistrationFormField placeholder="Username" />
            <RegistrationFormField placeholder="Password" />

            <div className="password-warning">
                <span className="info-icon">i</span>
                <span className="warning-text">Password must be at least 6 characters.</span>
            </div>

            <RegistrationFormField placeholder="Confirm Password" />

            <div className="registration-button-container">
                <button className="registration-button">
                    Create account
                </button>
            </div>
            <div className="already-have-account-message-div">
                <p className="already-have-account-message">
                    Already have an account? <Link className="already-have-account-sign-in-link" to={"/user/login"}>Sign in</Link>
                </p>
            </div>
        </div>
    );
}