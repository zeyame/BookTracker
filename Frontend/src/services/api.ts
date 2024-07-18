// File handles API calls to Flask backend 
import { book } from "../interfaces/BookInterface";

const BASE_URL: string = "http://127.0.0.1:5000";       // flask server


export const fetchDefaultBooks = async () => {

    const genres: Array<string> = ['romance', 'fiction', 'thriller', 'action', 'mystery', 'history', 'scifi'];

    const fetchPromises = genres.map(genre => 
        fetchBooksByGenre(genre)
    );

    const books = await Promise.all(fetchPromises);

    const defaultBooks: Map<string, Array<book>> = new Map();

    genres.forEach((genre, index) => {
        defaultBooks.set(genre.charAt(0).toUpperCase() + genre.slice(1), books[index]);
    });

    return defaultBooks;
}

const fetchBooksByGenre = async (genre : string) => {
    try {
        const response = await fetch(`${BASE_URL}/${genre}-books?limit=20`);
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