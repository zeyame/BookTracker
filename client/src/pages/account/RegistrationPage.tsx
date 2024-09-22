import React, { useEffect, useState } from "react";
import isEmail from 'validator/lib/isEmail';
import '../../styles/registration-page.css'
import { RegistrationForm } from "../../components/Registration-Page/RegistrationForm";
import { useLocation, useNavigate } from "react-router-dom";
import { RegistrationError } from "../../interfaces/RegistrationError";
import { registerUser, validateUser } from "../../services/userAccount";
import { handleKeyDown } from "../../utils/handleKeyDown";
import { validate } from "uuid";
import { UserRegistration } from "../../interfaces/UserRegistration";


export const RegistrationPage: React.FC = () => {

    const navigate = useNavigate();
    const location = useLocation();

    const errorRegisteringAfterVerification: string | null = location.state?.errorRegisteringAfterVerification;

    // states
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

    useEffect(() => {
        const possibleUserRegistration: string | null = sessionStorage.getItem("userRegistration"); 

        if (possibleUserRegistration) {
            const existingUserRegistrationDetails: UserRegistration = JSON.parse(possibleUserRegistration);
            setUseUserRegistration(existingUserRegistrationDetails);
        }
        
    }, []);


    // functions 

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
                await validateUser(useUserRegistration.email, useUserRegistration.username, useUserRegistration.password);

                // once data validated on both the client and server we save it to session storage for automatic re-filling of form
                sessionStorage.setItem("userRegistration", JSON.stringify(useUserRegistration));

                navigate('/user/verification', {state: {email: useUserRegistration.email, username: useUserRegistration.username, password: useUserRegistration.password}});
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

        else if (!isEmail(email, {allow_utf8_local_part: true})) {
            setRegistrationError(prev => ({
                ...prev,
                emailError: "Invalid email address."
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
            {
                errorRegisteringAfterVerification &&
                <div className="error-registering-after-verification">
                    {errorRegisteringAfterVerification}
                </div>
            }
            <h1 className="registration-page-app-name">Shelf Quest</h1>
            <RegistrationForm userRegistration={useUserRegistration} handleUserRegistration={handleUserRegistration} handleCreateAccount={handleCreateAccount} handleKeyDown={handleKeyDown} loading={loading} registrationError={registrationError} />
        </div>
    );
}