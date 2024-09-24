import React, { useEffect, useState } from "react";
import "../../styles/book-list-by-status.css";
import { SearchBarByStatus } from "./SearchBarByStatus";
import { ReadingStatus } from "../../interfaces/ReadingStatus";     // enum
import { BookByStatus } from "./BookByStatus";
import { book } from "../../interfaces/BookInterface";
import { BookWithStatus } from "../../interfaces/BookWithStatus";
import { Link } from "react-router-dom";

interface BookListByStatusProps {
    status: string
    handleSelectedBook: (book: BookWithStatus) => void
}

export const BookListByStatus: React.FC<BookListByStatusProps> = ({status, handleSelectedBook}) => {

    const [storedBooks, setStoredBooks] = useState<Array<BookWithStatus>>([]);

    // depending on status, get the stored books
    useEffect(() => {
        const storedBooks: string | null = localStorage.getItem("booksWithStatus");
        
        if (storedBooks) {
            const booksRecord: Record<string, BookWithStatus> = JSON.parse(storedBooks);
            const books: Array<BookWithStatus> = Object.values(booksRecord)
                .filter(bookWithStatus => bookWithStatus.status === status);

            if (books.length > 0) {
                handleSelectedBook(books[0]);
            }

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
                storedBooks.length > 0 ? 
                <>
                    <h2 className="book-list-by-status-title">
                        {status === ReadingStatus.CurrentlyReading ? "Your current reads" : status === ReadingStatus.WantToRead ? "Your future reads" : "Your past reads"}
                    </h2>
                    {
                        storedBooks.map(book =>
                            <BookByStatus key={book.bookData.id} bookWithStatus={book} handleSelectedBook={handleSelectedBook} />
                        )
                    }
                </>
                :
                <Link to={"/"}>
                    <div className="no-books-in-list-message">
                        Start by adding some books.
                    </div>
                </Link>
            }
        </div>  
    )
}