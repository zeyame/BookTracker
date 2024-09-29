// File handles fetching the initially displayed books for the user 
import { BASE_URL } from "../global-variables/BaseUrl";
import { book } from "../interfaces/BookInterface";

export const fetchDefaultBooks = async (limit: number, genres: Array<string>) => {
    try {
        const fetchPromises = genres.map(genre => {
            const encodedGenre = encodeURIComponent(genre);
            return fetchBooksByGenre(encodedGenre, limit);
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
        const response = await fetch(`${BASE_URL}/api/books/${genre}?limit=${limit}`);
        if (!response.ok) {
            throw new Error(`Response from Flask backend failed when requesting ${genre} books.`);
        }
        const data = await response.json();
        const books = data.books;
        return books;
    }
    catch (error) {
        throw error;
    }
}