import React, { useEffect, useState } from "react";
import "../../styles/book-list-by-status.css";
import { SearchBarByStatus } from "./SearchBarByStatus";
import { ReadingStatus } from "../../interfaces/ReadingStatus";     // enum
import { BookByStatus } from "./BookByStatus";
import { BookWithStatus } from "../../interfaces/BookWithStatus";
import { Link } from "react-router-dom";
import { book } from "../../interfaces/BookInterface";
import { getUserBooksByStatus } from "../../services/userBookService";

interface BookListByStatusProps {
    status: string;
    handleSelectedBook: (book: BookWithStatus) => void;
    refreshTrigger: number;
}

export const BookListByStatus: React.FC<BookListByStatusProps> = ({status, handleSelectedBook, refreshTrigger}) => {

    const [storedBooks, setStoredBooks] = useState<Array<BookWithStatus>>([]);
    const [filteredBooks, setFilteredBooks] = useState<Array<BookWithStatus>>([]);
    const [searchValue, setSearchValue] = useState<string>('');

    // depending on status, get the stored books
    useEffect(() => {
        const fetchBooks = async () => {
            const token = sessionStorage.getItem("token");
            if (!token) {
                console.error("No access token.");
                return;
            }

            try {
                const storedBooks: Array<book> = await getUserBooksByStatus(status);
                const booksWithStatus: BookWithStatus[] = storedBooks.map(book => ({
                    bookData: book,
                    status: status
                }));

                if (booksWithStatus.length > 0) {
                    handleSelectedBook(booksWithStatus[0]);
                }

                setStoredBooks(booksWithStatus);
            } catch (error) {
                console.error("Failed to fetch books from backend:", error);
            }
        };

        fetchBooks();

        return () => {
            setStoredBooks([]);
        };
    }, [status, refreshTrigger]); 


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