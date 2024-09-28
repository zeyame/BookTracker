import React, { useState } from "react"
import { BookListByStatus } from "../../components/Global/BookListByStatus"
import { BookPageByStatus } from "../../components/Global/BookPageByStatus"
import "../../styles/reading-page.css";
import { ReadingStatus } from "../../interfaces/ReadingStatus";
import { book } from "../../interfaces/BookInterface";
import { BookWithStatus } from "../../interfaces/BookWithStatus";
import { useAuthRedirect } from "../../custom-hooks/useAuthRedirect";

export const ToReadPage: React.FC = () => {
    useAuthRedirect();

    const [selectedBook, setSelectedBook] = useState<BookWithStatus | null>(null);

    const handleSelectedBook = (book: BookWithStatus) => {
        setSelectedBook(book);
    }

    return (
        <div className="reading-page-container">
            <BookListByStatus status={ReadingStatus.WantToRead} handleSelectedBook={handleSelectedBook} />
            {
                selectedBook && <BookPageByStatus book={selectedBook} />
            }
        </div>
    );
}