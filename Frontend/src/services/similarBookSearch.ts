import { book } from "../interfaces/BookInterface";

const BASE_URL: string = "http://127.0.0.1:5000";       // flask server

export const fetchSimilarBooks = async (title: string, limit: number): Promise<Array<book> | null> => {
    try {
        const response = await fetch(`${BASE_URL}/similar-books`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                title,
                type: 'book',
                limit
            })
        });

        if (!response.ok) {
            throw new Error("Unexpected error occurred when requesting similar books from the server.");
        }
        const data = await response.json();
        if (data.message) {
            console.log(data.message);
            return null;
        }
        else {
            const similarBooks: Array<book> = data.similarBooks;
            return similarBooks;
        }
    }
    catch (error: any) {    
        throw error;
    }
}