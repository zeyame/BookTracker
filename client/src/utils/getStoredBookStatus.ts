import { book } from "../interfaces/BookInterface";
import { BookWithStatus } from "../interfaces/BookWithStatus";

// gets the current reading status of a book
export function getStoredBookStatus (book: book | null): string {
    if (!book) {
        return "";
    }

    let username: string;
    
    const storedUser: string | null = sessionStorage.getItem("userLogin");
    if (storedUser) {
        const userDetails: UserLogin = JSON.parse(storedUser);
        username = userDetails.username;

        const storedBooksWithStatus: string | null = localStorage.getItem(`booksWithStatus-${username}`);
    
        if (storedBooksWithStatus) {
            try {
                const books: Record<string, BookWithStatus> = JSON.parse(storedBooksWithStatus);
                const bookWithStatus = books[book.id];
                return bookWithStatus?.status || "";
            }
            catch (error: any) {
                console.error("Error parsing stored books to get reading status.");
                return "";
            }
        }
    }
    else {
        console.error("No user logged in.");
    }

    return "";
}