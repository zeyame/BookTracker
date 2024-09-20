import React from "react";

interface LoginFormFieldProps {
    placeholder: string
    handleLoginField: (inputValue: string, inputFieldName: string) => void 
    error: string
}


export const LoginFormField: React.FC<LoginFormFieldProps> = ({placeholder, handleLoginField, error}) => {
    return (
        <div className="login-form-field-container">
            <input className="login-field-input" placeholder={placeholder} onChange={(inputValue) => handleLoginField(placeholder,inputValue.target.value)} />
            {
                    error.length > 0 
                &&
                    <div className="login-error">
                        <p>{error}</p>
                    </div>
            }
        </div>
    )
}