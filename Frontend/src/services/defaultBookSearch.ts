// File handles fetching the initially displayed books for the user 
import { book } from "../interfaces/BookInterface";

const BASE_URL: string = "http://127.0.0.1:5000";       // flask server

const genres: Array<string> = ['romance', 'fiction', 'thriller', 'action', 'mystery', 'history', 'horror', 'fantasy'];

export const fetchDefaultBooks = async () => {
    try {
        const fetchPromises = genres.map(genre => {
            const encodedGenre = encodeURIComponent(genre);
            return fetchBooksByGenre(encodedGenre, 9);
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

export const fetchBooksByGenre = async (genre : string, limit: number) => {
    try {
        const response = await fetch(`${BASE_URL}/${genre}-books?limit=${limit}`);
        if (!response.ok) {
            throw new Error(`Response from Flask backend failed when requesting ${genre} books.`);
        }
        const data = await response.json();
        const books: Array<book> = data.books;
        return books;
    }
    catch (error) {
        throw error;
    }
}