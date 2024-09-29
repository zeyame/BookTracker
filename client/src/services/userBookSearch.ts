// File handles fetching a book by either title, author, ISBN 
import { BASE_URL } from "../global-variables/BaseUrl";
import { book } from "../interfaces/BookInterface";

export const getBooks = async (search: string, limit: number, signal?: AbortSignal) => {
    try {
        const response = await fetch(`${BASE_URL}/api/books?search=${search}&limit=${limit}`, { signal });
        if (!response.ok) {
            throw new Error(`Failed to fetch books from the backend for search: ${search}`);
        }

        // fetched data can be a single book if ISBN was used for search or multiple books (5) if title or author name was used
        const data = await response.json();
        const books: Array<book> = data.books;
        return books;
    }
    catch (error: any) {
        throw error;
    }
}
