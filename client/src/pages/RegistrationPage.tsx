import React from "react";
import '../styles/registration-page.css'
import { RegistrationForm } from "../components/Registration-Page/RegistrationForm";

export const RegistrationPage: React.FC = () => {

    return (
        <div className="registration-page-container">
            <h1 className="registration-page-app-name">Shelf Quest</h1>
            <RegistrationForm />
        </div>
    );
}