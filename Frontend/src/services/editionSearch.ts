import { edition } from "../interfaces/EditionInterface";

const BASE_URL: string = "http://127.0.0.1:5000";       // flask server

export const fetchEditions = async (bookTitle: string, bookAuthors: Array<string>) => {
    try {
        const response = await fetch(`${BASE_URL}/editions?title=${bookTitle}&authors=${bookAuthors}`);
        if (!response.ok) {
            throw new Error(`Response from Flask server failed when requesting editions for book with title ${bookTitle}`);
        }
        const editions: Array<edition> = await response.json();
        return editions;
    }
    catch (error: any) {
        throw error;
    }
}