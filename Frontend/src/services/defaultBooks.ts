// File handles fetching the initially displayed books for the user 
import { book } from "../interfaces/BookInterface";

const BASE_URL: string = "http://127.0.0.1:5000";       // flask server


export const fetchDefaultBooks = async () => {

    const genres: Array<string> = ['romance', 'fiction', 'thriller', 'action', 'mystery', 'history', 'scifi', 'horror', 'fantasy'];

    if (sessionStorage.getItem('defaultBooksCache')) {
        const defaultBooksCache: string | null = sessionStorage.getItem('defaultBooksCache');
        if (defaultBooksCache) {
            const parsedCache = JSON.parse(defaultBooksCache);  // parse object corresponding to original default books map as a js object
            return new Map<string, Array<book>>(Object.entries(parsedCache));
        }
    }

    const fetchPromises = genres.map(genre => {
        const encodedGenre = encodeURIComponent(genre);
        return fetchBooksByGenre(encodedGenre, 0);
    });

    const books = await Promise.all(fetchPromises);

    const defaultBooks: Map<string, Array<book>> = new Map();

    genres.forEach((genre, index) => {
        defaultBooks.set(genre, books[index]);
    });

    // Saving default books fetched as cache data lasting for a single session
    const defaultBooksObject = Object.fromEntries(defaultBooks);
    sessionStorage.setItem('defaultBooksCache', JSON.stringify(defaultBooksObject));

    return defaultBooks;
}

export const fetchBooksByGenre = async (genre : string, offset: number) => {
    try {
        const response = await fetch(`${BASE_URL}/${genre}-books?limit=7&offset=${offset}`);
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