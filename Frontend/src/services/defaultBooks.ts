// File handles fetching the initially displayed books for the user 
import { book } from "../interfaces/BookInterface";

const BASE_URL: string = "http://127.0.0.1:5000";       // flask server

const genres: Array<string> = ['romance', 'fiction', 'thriller', 'action', 'mystery', 'history', 'scifi', 'horror', 'fantasy'];

export const fetchDefaultBooks = async () => {
    try {
        const fetchPromises = genres.map(genre => {
            const encodedGenre = encodeURIComponent(genre);
            return fetchBooksByGenre(encodedGenre);
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

export const fetchBooksByGenre = async (genre : string) => {
    try {
        const response = await fetch(`${BASE_URL}/${genre}-books?limit=7`);
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

// requests cache to be setup on the server
export const initializeCaching = async () => {
    try {
        const response = await fetch(`${BASE_URL}/cache?limit=7`);
        if (!response.ok) {
            throw new Error(`Server failed to set up the cache`);
        }
        console.log(await response.json());         // logging the cache if response was ok
    }
    catch (error: any) {
        throw error;
    }
}

// retrieves 7 cached books for a specific genre which the user requested
export const getCachedBooks = async (genreName: string) => {
    try {
        const response = await fetch(`${BASE_URL}/cached-${genreName}`);
        if (!response.ok) {
            throw new Error(`Response from backend failed when retrieving cached books for ${genreName} genre`);
        }
        const cachedBooks: Array<book> = await response.json();
        return cachedBooks;
    }
    catch (error: any) {
        throw error;
    }
}

// requests more books to be cached for a specific genre
export const updateCache = async (genreName: string) => {
    try {
        const response = await fetch(`${BASE_URL}/update-${genreName}-cache?limit=7`);
        if (!response.ok) {
            throw new Error(`Failed response from the backend when requested to update the cache for ${genreName} genre.`);
        }
        console.log(await response.json());
    }
    catch (error: any) {
        throw error;
    }
}