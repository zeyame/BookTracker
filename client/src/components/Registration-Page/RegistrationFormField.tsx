import React from "react";

interface FieldProps {
    placeholder: string
}

export const RegistrationFormField: React.FC<FieldProps> = ({placeholder}) => {
    return (
        <div className="registration-form-field-container">
            <input className="registration-field-input" placeholder={placeholder} />
        </div>
    );
}