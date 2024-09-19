import React from "react";

interface FieldProps {
    placeholder: string
    handleInputField: (inputFieldName: string, inputValue: string) => void
    error: string
}

export const RegistrationFormField: React.FC<FieldProps> = ({placeholder, handleInputField, error}) => {
    return (
        <div className="registration-form-field-container">
            <input className="registration-field-input" placeholder={placeholder} onChange={(inputValue) => handleInputField(placeholder,inputValue.target.value)} />
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