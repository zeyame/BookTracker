import React, { useState } from "react"
import { useLocation } from "react-router-dom";
import '../../styles/login-page.css';
import { LoginForm } from "../../components/Login-Page/LoginForm";
import { LoginError } from "../../interfaces/LoginError";

interface UserLogin {
    username: string
    password: string
}

export const LoginPage: React.FC = () => {
    const location = useLocation();
    const registeredMessage: string | null = location.state?.registeredMessage;

    const [userLogin, setUserLogin] = useState<UserLogin>({
        username: '',
        password: '',
    });
    
    const [loginError, setLoginError] = useState<LoginError>({
        usernameError: '',
        passwordError: ''
    });

    const [loading, setLoading] = useState<boolean>(false);

    
    const handleLoginField = (inputFieldName: string, inputValue: string) => {
        const adjustedInputFieldName: string = inputFieldName.replace(/\s+/g, '').toLowerCase();

        if (adjustedInputFieldName in userLogin) {
            setUserLogin(prev => ({
                ...prev,
                [adjustedInputFieldName]: inputValue
            }));
        }
    }

    const handleLoginButton = () => {
        setLoginError(prev => ({
            usernameError: '',
            passwordError: ''
        }));
    }

    return (
        <div className="login-page-container">
            {
                registeredMessage ?
                <div className="successful-verification-message">
                    {registeredMessage}
                </div>
                :
                <div className="modal-will-be-here">
                    Account verification successful. You can now login. 
                </div>
            }
            <h1 className="login-page-app-name">Shelf Quest</h1>
            <LoginForm handleLoginField={handleLoginField} handleLoginButton={handleLoginButton} loading={loading} loginError={loginError} />
        </div>
    );
}