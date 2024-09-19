import React, { useState } from "react";
import '../../styles/registration-page.css'
import { RegistrationForm } from "../../components/Registration-Page/RegistrationForm";
import { useNavigate } from "react-router-dom";
import { RegistrationError } from "../../interfaces/RegistrationError";
import { registerUser } from "../../services/userAccount";

interface UserRegistration {
    email: string
    username: string
    password: string
    confirmpassword: string
}

export const RegistrationPage: React.FC = () => {

    const navigate = useNavigate();

    const [useUserRegistration, setUseUserRegistration] = useState<UserRegistration>({
        email: '',
        username: '',
        password: '',
        confirmpassword: ''
    });
    const [loading, setLoading] = useState<boolean>(false);
    const [registrationError, setRegistrationError] = useState<RegistrationError>({
        emailError: '',
        usernameError: '',
        passwordError: '',
        otherError: ''
    });


    const handleUserRegistration = (inputFieldName: string, inputValue: string) => {
        const adjustedInputFieldName: string = inputFieldName.replace(/\s+/g, '').toLowerCase();

        if (adjustedInputFieldName in useUserRegistration) {
            setUseUserRegistration(prev => ({
                ...prev,
                [adjustedInputFieldName]: inputValue
            }));
        }
    }


    const handleCreateAccount = async () => {
        // reset previous errors
        setRegistrationError({
            emailError: '',
            usernameError: '',
            passwordError: '',
            otherError: ''
        });


        // validate the form
        const formHasErrors: boolean = checkFormValidity();

        // send data to server for user registration if data is valid
        if (!formHasErrors) {
            try {
                setLoading(true)
                await registerUser(useUserRegistration.email, useUserRegistration.username, useUserRegistration.password);
                navigate('/user/verification', {state: {email: useUserRegistration.email, username: useUserRegistration.username}});
            }
            catch (error: any) {
                const errorMessage: string = error.message;
    
                if (errorMessage.includes("email")) {
                    setRegistrationError(prev => ({
                        ...prev,
                        emailError: errorMessage
                    }));
                }
    
                else if (errorMessage.includes("username")) {
                    setRegistrationError(prev => ({
                        ...prev,
                        usernameError: errorMessage
                    }));
                }
    
                else { 
                    setRegistrationError(prev => ({
                        ...prev,
                        otherError: errorMessage
                    }));
                }
            }
            finally {
                setLoading(false);
            }
        }
    }


    const checkFormValidity = (): boolean => {
        let hasErrors = false;

        const { email, username } = useUserRegistration;       

        if (email.length < 1) {
            setRegistrationError(prev => ({
                ...prev,
                emailError: "Email is required."
            }));
            hasErrors = true;
        }

        if (username.length < 1) {
            setRegistrationError(prev => ({
                ...prev,
                usernameError: "Username is requied."
            }));
            hasErrors = true;
        }

        const passwordHasErrors: boolean = checkPasswordValidity();

        if (passwordHasErrors) hasErrors = true;

        return hasErrors;
    }

    const checkPasswordValidity = ():boolean => {

        let hasErrors = false;

        const { password, confirmpassword } = useUserRegistration;
        

        console.log(password);
        console.log(confirmpassword);

        if (password.length < 1) {
            setRegistrationError(prev => ({
                ...prev,
                passwordError: "Password is required."
            }));
            hasErrors = true;
        }

        else if (password.length < 6) {
            setRegistrationError(prev => ({
                ...prev,
                passwordError: "Password must be atleast 6 characters."
            }));
            hasErrors = true;
        }
        else if (password !== confirmpassword) {
            setRegistrationError(prev => ({
                ...prev,
                passwordError: "Passwords must match."
            }));
            hasErrors = true;
        }

        return hasErrors;
    }
    
    return (
        <div className="registration-page-container">
            <h1 className="registration-page-app-name">Shelf Quest</h1>
            <RegistrationForm handleUserRegistration={handleUserRegistration} handleCreateAccount={handleCreateAccount} loading={loading} registrationError={registrationError} />
        </div>
    );
}