import React, { useEffect, useState } from "react";
import "../../styles/book-list-by-status.css";
import { SearchBarByStatus } from "./SearchBarByStatus";
import { ReadingStatus } from "../../interfaces/ReadingStatus";     // enum
import { BookByStatus } from "./BookByStatus";
import { book } from "../../interfaces/BookInterface";
import { BookWithStatus } from "../../interfaces/BookWithStatus";

interface BookListByStatusProps {
    status: string
}

export const BookListByStatus: React.FC<BookListByStatusProps> = ({status}) => {

    const [storedBooks, setStoredBooks] = useState<Array<BookWithStatus>>([]);

    // depending on status, get the stored books
    useEffect(() => {
        const storedBooks: string | null = localStorage.getItem("booksWithStatus");
        
        if (storedBooks) {
            const booksRecord: Record<string, BookWithStatus> = JSON.parse(storedBooks);
            const books: Array<BookWithStatus> = Object.values(booksRecord)
                .filter(bookWithStatus => bookWithStatus.status === status);

            setStoredBooks(books);
        }
    
        return () => {
            setStoredBooks([]);
        }
    }, [status]);

    return (
        <div className="book-list-by-status-container">
            <SearchBarByStatus status={status} />
            {
                storedBooks.length > 0 && storedBooks.map(book =>
                    <BookByStatus key={book.bookData.id} bookWithStatus={book} />
                )
            }
        </div>  
    )
}