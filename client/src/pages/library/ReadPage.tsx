import React, { useState } from "react"
import { BookListByStatus } from "../../components/Global/BookListByStatus"
import { BookPageByStatus } from "../../components/Global/BookPageByStatus"
import "../../styles/reading-page.css";
import { ReadingStatus } from "../../interfaces/ReadingStatus";
import { book } from "../../interfaces/BookInterface";
import { BookWithStatus } from "../../interfaces/BookWithStatus";
import { useAuthRedirect } from "../../utils/useCheckForToken";

export const ReadPage: React.FC = () => {
    useAuthRedirect();

    const [selectedBook, setSelectedBook] = useState<BookWithStatus | null>(null);

    const handleSelectedBook = (book: BookWithStatus) => {
        setSelectedBook(book);
    }

    return (
        <div className="reading-page-container">
            <BookListByStatus status={ReadingStatus.Read} handleSelectedBook={handleSelectedBook} />
            {
                selectedBook && <BookPageByStatus book={selectedBook} />
            }
        </div>
    )
}