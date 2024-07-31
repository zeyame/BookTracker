import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { book } from "../interfaces/BookInterface";
import { SearchBar } from "../components/SearchBar";
import '../styles/book-page.css';
import { LoadingIcon } from "../components/LoadingIcon";

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
                    <img className="book-page-book-cover" src={book.image_url} alt="Book cover" />
                    <button className="reading-status-btn">Want to read</button>
                    <button className="buy-amazon-btn">Buy on Amazon</button>
                </div>
                <div className="book-page-main-content">
                    <div className="book-page-title-section">
                        <h1 className="book-title-header">{book.title}</h1>
                    </div>
                    <div className="book-page-metadata-section">
                        <div className="book-page-authors">
                            {book.authors.map((author, index) => 
                                <h3 className="book-page-author" key={author}>{index > 0 && ', '}{author}</h3>
                            )}
                        </div>
                        <div className="book-page-description">
                            <p>{book.description}</p>
                        </div>
                        {book.categories.length > 0 &&
                            <div className="book-page-genres">
                                <p>Genres:</p> 
                                {book.categories.map(category =>
                                    <p className="book-page-genre" key={category}>{category}</p>
                                )}
                            </div>
                        }
                        <div className="edition-details">
                            <p className="edition-details-title">This edition</p>
                            <p className="page-count">Page count: {book.pageCount}</p>
                            <p className="published">Published {book.publishedDate} by {book.publisher}</p>
                            <p className="language">Language: {book.language === 'en' ? 'English' : `${book.language}`}</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}