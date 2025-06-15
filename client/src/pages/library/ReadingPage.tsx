import React, { useState } from "react"
import { BookListByStatus } from "../../components/Global/BookListByStatus"
import { BookPageByStatus } from "../../components/Global/BookPageByStatus"
import "../../styles/reading-page.css";
import { ReadingStatus } from "../../interfaces/ReadingStatus";
import { BookWithStatus } from "../../interfaces/BookWithStatus";
import { useAuthRedirect } from "../../custom-hooks/useAuthRedirect";

export const ReadingPage: React.FC = () => {
    useAuthRedirect();

    const [selectedBook, setSelectedBook] = useState<BookWithStatus | null>(null);
    const [refreshTrigger, setRefreshTrigger] = useState<number>(0);

    const handleBookStatusChange = () => {
        setSelectedBook(null);
        setRefreshTrigger(prev => prev + 1);
    };


    const handleSelectedBook = (book: BookWithStatus) => {
        setSelectedBook(book);
    }

    return (
        <div className="reading-page-container">
            <BookListByStatus status={ReadingStatus.CurrentlyReading} handleSelectedBook={handleSelectedBook} refreshTrigger={refreshTrigger} />
            {
                selectedBook && <BookPageByStatus book={selectedBook} onStatusChange={handleBookStatusChange}/>
            }
        </div>
    )
}