import { book } from "../interfaces/BookInterface";
import { BookWithStatus } from "../interfaces/BookWithStatus";

export const updateBookStatus = (book: book, bookStatus: string, fullAuthorDescriptionRef?: string) => {
    let username: string;
    
    const storedUser: string | null = sessionStorage.getItem("userLogin");
    if (storedUser) {
        const userDetails: UserLogin = JSON.parse(storedUser);
        username = userDetails.username;
    
        const storedBooksWithStatus: string | null = localStorage.getItem(`booksWithStatus-${username}`);

        let books: Record<string, BookWithStatus> = storedBooksWithStatus ? JSON.parse(storedBooksWithStatus) : {};

        books[book.id] = {bookData: book, status: bookStatus, authorDescription: fullAuthorDescriptionRef};

        localStorage.setItem(`booksWithStatus-${username}`, JSON.stringify(books));
    }
}