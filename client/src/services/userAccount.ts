import { json } from "react-router-dom";

const BASE_URL: string = "http://localhost:8080";       // Spring server


export const registerUser = async (email: string, username: string, password: string): Promise<void> => {

    try {
        const response = await fetch(`${BASE_URL}/api/users/register`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email,
                username: username,
                password: password
            })
        });

        if (!response.ok) {
            const errorData = await response.json();

            switch (response.status) {
                case 409:
                    throw new Error(errorData.message);
                
                case 500:
                    throw new Error("An unexpected error occurred. Please try again later.");
                
                default:
                    throw new Error("An error occurred during registration. Please try again later.");
            }
        }
    }
    catch (error: any) {
        throw error;
    }
}


export const requestOTP = async (email: string, username: string): Promise<void> => {
    try {
        const response = await fetch(`${BASE_URL}/api/otp/send`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                email: email,
                username: username
            })
        });

        if (!response.ok) {
            throw new Error("An error occurred while sending the OTP.");
        }

    }
    catch (error) {
        throw error;
    }
}

