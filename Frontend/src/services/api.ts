// File handles API calls to Flask backend 
import { book } from "../BookInterface";

const BASE_URL: string = "http://127.0.0.1:5000";       // flask server

// Function is responsible for fetching a set of random books in the romance category
export const fetchRomanceBooks = async () => {
    try {
        const response = await fetch(`${BASE_URL}/romance-books?limit=20`);
        if (!response.ok) {
            throw new Error("Response from Flask backend failed.");
        }
        const data: Array<book> = await response.json();
        return data;
    }
    catch (error) {
        console.error(error);
        throw error;
    }
}