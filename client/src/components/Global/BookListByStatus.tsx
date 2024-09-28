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
    const [filteredBooks, setFilteredBooks] = useState<Array<BookWithStatus>>([]);
    const [searchValue, setSearchValue] = useState<string>('');

    // depending on status, get the stored books
    useEffect(() => {
        let username: string;

        const storedUser: string | null = sessionStorage.getItem("userLogin");
        if (storedUser) {
            const userDetails: UserLogin = JSON.parse(storedUser);
            username = userDetails.username;

            const storedBooks: string | null = localStorage.getItem(`booksWithStatus-${username}`);
            
            if (storedBooks) {
                const booksRecord: Record<string, BookWithStatus> = JSON.parse(storedBooks);
                const books: Array<BookWithStatus> = Object.values(booksRecord)
                    .filter(bookWithStatus => bookWithStatus.status === status);
    
                if (books.length > 0) {
                    handleSelectedBook(books[0]);
                }
    
                setStoredBooks(books);
            }

        }
        else {
            console.error("User not logged in");
        }
    
        return () => {
            setStoredBooks([]);
        }
    }, [status]);

    useEffect(() => {
        if (searchValue) {
            const filtered: Array<BookWithStatus> = storedBooks.filter(book => 
                book.bookData.title.toLowerCase().includes(searchValue.toLowerCase())
            );
            setFilteredBooks(filtered);
        }
        else {
            setFilteredBooks(storedBooks);
        }
    }, [searchValue, storedBooks]);

    const handleSearchBarInput = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSearchValue(event.target.value);
    }

    return (
        <div className="book-list-by-status-container">
            <SearchBarByStatus status={status} handleSearchBarInput={handleSearchBarInput} />

            {
                filteredBooks.length > 0 ? 
                <>
                    <h2 className="book-list-by-status-title">
                        {status === ReadingStatus.CurrentlyReading 
                            ? "Your current reads" 
                            : status === ReadingStatus.WantToRead 
                            ? "Your future reads" 
                            : "Your past reads"
                        }
                    </h2>
                    {
                        filteredBooks.map(book =>
                            <BookByStatus 
                                key={book.bookData.id} 
                                bookWithStatus={book} 
                                handleSelectedBook={handleSelectedBook} 
                            />
                        )
                    }
                </>
                :
                searchValue.length > 0 ? 
                <div className="book-list-by-status-no-books-found">No books found.</div>
                :
                <Link to={"/app"}>
                    <div className="no-books-in-list-message">
                        Start by adding some books.
                    </div>
                </Link>
            }
        </div>  
    )
}