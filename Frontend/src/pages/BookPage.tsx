import React from "react";
import { useLocation } from "react-router-dom";
import { book } from "../interfaces/BookInterface";
import { SearchBar } from "../components/SearchBar";
import '../styles/book-page.css';

export const BookPage: React.FC = () => {

    const location = useLocation();
    const book: book | undefined = location.state.bookData;

    if (!book) {
        return (
            <div>Book Not Found.</div>
        );
    }

    return (
        <div className="book-page-container">
            <SearchBar />
            <div className="book-page-main">
                <div className="book-page-left-column">
                    <img className="book-cover" src={book.image_url} alt="Book cover" />
                </div>
                <div className="book-page-main-content">
                    <div className="book-page-title-section">

                    </div>
                    <div className="book-page-metadata-section">

                    </div>
                </div>
            </div>
        </div>
    );
}