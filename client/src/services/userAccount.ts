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


