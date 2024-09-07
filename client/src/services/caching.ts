// File handles fetching the initially displayed books for the user 
import { book } from "../interfaces/BookInterface";

const BASE_URL: string = "http://127.0.0.1:5000";       // flask server

// requests cache to be setup on the server
export const initializeCaching = async () => {
    try {
        const response = await fetch(`http://127.0.0.1:8080/api/books/cache`);
        if (!response.ok) {
            throw new Error(`Server failed to set up the cache`);
        }
        const data = await response.json();
        console.log(data.message);         // logging the cache if response was ok
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
        const data = await response.json();
        const cachedBooks: Array<book> = data.cachedBooks;
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
        const data = await response.json();
        console.log(data.message);
    }
    catch (error: any) {
        throw error;
    }
}