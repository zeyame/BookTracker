// File handles fetching the initially displayed books for the user 
import { book } from "../interfaces/BookInterface";

const BASE_URL: string = "http://127.0.0.1:8080";       // Spring server

// requests cache to be setup on the server
export const initializeCaching = async (limit: number) => {
    try {
        const response = await fetch(`${BASE_URL}/api/books/cache?limit=${limit}`);
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

// retrieves a specified number of cached books for a specific genre which the user requested
export const getCachedBooks = async (genreName: string, limit: number) => {
    try {
        const response = await fetch(`${BASE_URL}/api/books/${genreName}?limit=${limit}`);
        if (!response.ok) {
            throw new Error(`Response from backend failed when retrieving cached books for ${genreName} genre`);
        }
        const data: Array<book> = await response.json();
        return data;
    }
    catch (error: any) {
        throw error;
    }
}

// requests more books to be cached for a specific genre
export const updateCache = async (genreName: string, limit: number) => {
    try {
        const response = await fetch(`${BASE_URL}/api/books/cache/${genreName}/update?limit=${limit}`);
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