import { json } from "react-router-dom";

const BASE_URL: string = "http://localhost:8080";       // Spring server


export const validateUser = async (email: string, username: string, password: string): Promise<void> => {

    try {
        const response = await fetch(`${BASE_URL}/api/users/validate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                email,
                username,
                password
            })
        });

        if (!response.ok) {
            const errorData = await response.json();
            
            switch (response.status) {
                case 400:
                    throw new Error("Missing data from the user.");
                case 409:
                    throw new Error(errorData.message);
                default:
                    throw new Error("An unexpected error occurred with validating user data.");
            }
        }
    }
    catch (error: any) {
        if (error instanceof TypeError) {
            throw new Error("An unexpected error occurring when contacting server.");
        }
        throw error;
    }

}

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
        if (error instanceof TypeError) {
            // handling network related errors
            throw new Error("An unexpected error occurred when contacting server for registration.");
        }
        throw error;
    }
}


export const requestOTP = async (email: string): Promise<void> => {
    try {
        const response = await fetch(`${BASE_URL}/api/otp/send`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                email
            })
        });

        if (!response.ok) {
            throw new Error("An error occurred while sending the OTP.");
        }

    }
    catch (error) {
        if (error instanceof TypeError) {
            // handling network related errors
            throw new Error("An unexpected error occurred.");
        }
        throw error;
    }
}


export const verifyOtp = async (email: string, otp: string): Promise<void> => {

    try {
        const response = await fetch(`${BASE_URL}/api/otp/verify`,{
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                email,
                otp
            })
        });

        if (!response.ok) {
            const errorData = await response.json();
            switch (response.status) {
                case 500:
                    throw new Error("Unexpected error occurred when verifying OTP.");
                default:
                    throw new Error(errorData.message);
            }
        }
        
    }
    catch (error: any) {
        if (error instanceof TypeError) {
            // handling network related errors
            throw new Error("An unexpected error occurred.");
        }
        throw error;
    }
}

export const loginUser = async (username: string, password: string): Promise<void> => {
    try {
        const response = await fetch(`${BASE_URL}/api/users/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                username,
                password
            })
        });

        if (!response.ok) {
            const errorData = await response.json();

            switch(response.status) {
                case 401:
                    if (errorData.message.includes("verified")) {
                        throw new Error("You have not yet verified your account.");
                    }
                    else {
                        throw new Error(errorData.message);
                    }
                default:
                    throw new Error("An unexpected error occurred. Please try again.");
            }
        }

        const data = await response.json();

        // generated JWT once user is logged in
        const token = data.token;

        // store token in session storage
        sessionStorage.setItem('token', token);
    }
    catch (error: any) {
        if (error instanceof TypeError) {
            // handling network related errors
            throw new Error("An unexpected error occurred.");
        }
        throw error;
    }
}
