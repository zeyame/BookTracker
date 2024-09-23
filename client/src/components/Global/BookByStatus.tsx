import React from "react";
import { book } from "../../interfaces/BookInterface";
import "../../styles/book-by-status-component.css";
import { BookWithStatus } from "../../interfaces/BookWithStatus";

interface BookByStatusProps {
    bookWithStatus: BookWithStatus
}

export const BookByStatus: React.FC<BookByStatusProps> = ({bookWithStatus}) => {
    return (
        <div className="book-by-status-container">
            <img className="book-by-status-cover" src={bookWithStatus.bookData.imageUrl} alt="Book cover" />
            <div className="book-by-status-title-container">
                <p className="book-by-status-title">
                    {bookWithStatus.bookData.title}
                </p>
                <div className="book-by-status-authors-container">
                    <p className="book-by-status-author">
                        {bookWithStatus.bookData.authors[0]}
                    </p>
                </div>
            </div>
        </div>
    );
}