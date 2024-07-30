import { edition } from "../interfaces/EditionInterface";

const BASE_URL: string = "http://127.0.0.1:5000";       // flask server

export const fetchEditions = async (volumeId: string) => {
    try {
        const response = await fetch(`${BASE_URL}/editions-${volumeId}`);
        if (!response.ok) {
            throw new Error(`Response from Flask server failed when requesting editions for book with volume id ${volumeId}`);
        }
        const editions: Array<edition> = await response.json();
        return editions;
    }
    catch (error: any) {
        throw error;
    }
}