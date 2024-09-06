// File handles fetching a book by either title, author, ISBN 
import { book } from "../interfaces/BookInterface";

const BASE_URL: string = "http://localhost:8080";       // Spring server


export const getBooks = async (search: string, signal?: AbortSignal) => {
    try {
        const response = await fetch(`${BASE_URL}/api/books?search=${search}`, { signal });
        if (!response.ok) {
            throw new Error(`Failed to fetch books from the backend for search: ${search}`);
        }

        // fetched data can be a single book if ISBN was used for search or multiple books (5) if title or author name was used
        const books: Array<book> = await response.json();
        
        return books;
    }
    catch (error: any) {
        throw error;
    }
}
