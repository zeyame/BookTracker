// File handles API calls to Flask backend 
import { book } from "../BookInterface";

const BASE_URL: string = "http://127.0.0.1:5000";       // flask server


export const fetchDefaultBooks = async () => {
    const romanceBooks = await fetchBooksByGenre('romance');
    const fictionBooks = await fetchBooksByGenre('fiction');
    const thrillerBooks = await fetchBooksByGenre('thriller');
    const actionBooks = await fetchBooksByGenre('action');
    const mysteryBooks = await fetchBooksByGenre('mystery');
    const historyBooks = await fetchBooksByGenre('history');
    const scifiBooks = await fetchBooksByGenre('scifi');

    const defaultBooks: Map<string, Array<book>> = new Map([
        ['Romance', romanceBooks],
        ['Fiction', fictionBooks],
        ['Thriller', thrillerBooks],
        ['Action', actionBooks],
        ['Mystery', mysteryBooks],
        ['History', historyBooks],
        ['Scifi', scifiBooks],
    ]);

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