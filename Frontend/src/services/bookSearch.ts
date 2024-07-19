// File handles fetching a book by either title, author, ISBN 
import { book } from "../interfaces/BookInterface";

const BASE_URL: string = "http://127.0.0.1:5000";       // flask server


export const getBook = async (search: string) => {
    try {
        const response = await fetch(`${BASE_URL}/book?search=${search}&limit=5`);
        if (!response.ok) {
            throw new Error(`Response from backend failed for fetching book with the user search ${search}`);
        }

        // fetched data can be a single book if ISBN was used for search or multiple books (5) if title or author name was used
        const data: book | Array<book> = await response.json();
        console.log(data);
    }
    catch (error) {
        console.error(error);
        throw error;
    }
}
