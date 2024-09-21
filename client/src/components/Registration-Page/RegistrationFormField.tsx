import React from "react";

interface FieldProps {
    placeholder: string
    handleInputField: (inputFieldName: string, inputValue: string) => void
    handleCreateAccount: () => void
    handleKeyDown: (event: React.KeyboardEvent<HTMLInputElement>, functionToCall: () => void) => void
    error: string
}

export const RegistrationFormField: React.FC<FieldProps> = ({placeholder, handleInputField, handleCreateAccount, handleKeyDown, error}) => {
    const isPasswordField: boolean = placeholder.toLowerCase() === 'password' || placeholder.toLowerCase() === 'confirm password';

    return (
        <div className="registration-form-field-container">
            <input type={isPasswordField ? 'password' : 'text'} className="registration-field-input" placeholder={placeholder} onChange={(inputValue) => handleInputField(placeholder,inputValue.target.value)} onKeyDown={(event) => handleKeyDown(event, handleCreateAccount)} />
            {
                    error.length > 0 
                &&
                    <div className="registration-error">
                        <p>{error}</p>
                    </div>
            }
        </div>
    );
}