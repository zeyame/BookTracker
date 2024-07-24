// File handles fetching the initially displayed books for the user 
import { book } from "../interfaces/BookInterface";

const BASE_URL: string = "http://127.0.0.1:5000";       // flask server

const genres: Array<string> = ['romance', 'fiction', 'thriller', 'action', 'mystery', 'history', 'scifi', 'horror', 'fantasy'];

export const fetchDefaultBooks = async () => {
    try {
        const fetchPromises = genres.map(genre => {
            const encodedGenre = encodeURIComponent(genre);
            return fetchBooksByGenre(encodedGenre, 0);
        });
        const books = await Promise.all(fetchPromises);

        const defaultBooks: Map<string, Array<book>> = new Map();
        genres.forEach((genre, index) => {
            defaultBooks.set(genre, books[index]);
        });
    
        return defaultBooks;
    }
    catch (error) {
        throw error;        // error handled in search page component
    }
}

export const cacheBooks = async () => {
    try {
        const response = await fetch(`${BASE_URL}/cache`);
        console.log(await response.json());
    }
    catch (error: any) {
        throw error;
    }
}

export const fetchBooksByGenre = async (genre : string, offset: number) => {
    try {
        const response = await fetch(`${BASE_URL}/${genre}-books?limit=7&offset=${offset}`);
        if (!response.ok) {
            throw new Error(`Response from Flask backend failed when requesting ${genre} books.`);
        }
        const data: Array<book> = await response.json();
        return data;
    }
    catch (error) {
        console.error(error);
        throw error;
    }
}