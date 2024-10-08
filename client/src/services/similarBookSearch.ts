import { BASE_URL } from "../global-variables/BaseUrl";
import { book } from "../interfaces/BookInterface";

export const fetchSimilarBooks = async (title: string, limit: number): Promise<Array<book> | null> => {
    try {
        const encodedTitle = encodeURIComponent(title.toLowerCase());
        const response = await fetch(`${BASE_URL}/api/books/similar?title=${encodedTitle}&type=book&limit=${limit}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });
        

        if (!response.ok) {
            throw new Error("Unexpected error occurred when requesting similar books from the server.");
        }

        // returned structure = {similarBooks: [], errors: { errors: [] }}
        const data = await response.json();
        const similarBooks: Array<book> = data.similarBooks;
        return similarBooks;
    }
    catch (error: any) {    
        throw error;
    }
}