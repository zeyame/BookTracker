import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { book } from "../interfaces/BookInterface";
import { SearchBar } from "../components/SearchBar";
import '../styles/book-page.css';
import { LoadingIcon } from "../components/LoadingIcon";
import { fetchSimilarBooks } from "../services/similarBookSearch";
import { SimilarBook } from "../components/Book-Page/SimilarBook";
import { AboutAuthor } from "../components/Book-Page/AboutAuthor";

export const BookPage: React.FC = () => {

    const location = useLocation();
    const book: book | undefined = location.state.bookData;
    
    // states
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<boolean>(false);
    const [similarBooks, setSimilarBooks] = useState<Array<book>>([]);

    // effects
    // fetches a specified number of similar books 
    useEffect(() => {
        const getSimilarBooks = async () => {
            if (book) {
                try {
                    if (sessionStorage.getItem(`${book.title}-similar-books`)) {
                        const storedSimilarBooks = sessionStorage.getItem(`${book.title}-similar-books`);
                        if (storedSimilarBooks) {
                            const similarBooks: Array<book> = JSON.parse(storedSimilarBooks);
                            setSimilarBooks(similarBooks);
                        }
                    }
                    else {
                        setLoading(true);
                        const similarBooks: Array<book> | null = await fetchSimilarBooks(book.title, 6);
                        if (similarBooks && similarBooks.length > 0) {
                            setSimilarBooks(similarBooks);
                            sessionStorage.setItem(`${book.title}-similar-books`, JSON.stringify(similarBooks));
                        }
                    }
                }   
                catch {
                    setError(true);
                }
                finally {
                    setLoading(false);
                }
            }
        }
        getSimilarBooks();

        return () => {
            setSimilarBooks([]);
        }
    }, [book]);
    

    // functions
    const sliceDescription = (description: string): string => {
        // Split the description into sentences using regex to account for various sentence endings
        const sentences = description.split(/(?<=[.!?])\s+/);
    
        // Slice the array to get the first 5 sentences
        const slicedSentences = sentences.slice(0, 5);
    
        // Join the sentences back into a single string
        return slicedSentences.join(' ');
    }

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

                        <AboutAuthor authorName={book.authors[0]} sliceDescription={sliceDescription} />

                        <div className="similar-books-container">
                            <p className="similar-books-header">Readers also enjoyed</p>
                            {
                                // request to fetch similar books in progress
                                loading ? 
                                    <div className="loading-similar-books">
                                        <p>Loading</p>
                                        <LoadingIcon />
                                    </div>
                                :
                                // request to fetch similar books unexpectedly fails
                                error ? 
                                    <div className="similar-books-error">
                                        <p className="about-author-error-message">Failed to fetch similar books. Please refresh to try again.</p>
                                    </div>
                                :
                                <div className="similar-books">
                                    {
                                        // attempting to fetch similar books successful but might return nothing if no books found
                                        similarBooks ?
                                            similarBooks.map(book => 
                                                <SimilarBook key={book.id} book={book} />
                                            )
                                        :
                                        <div className="no-similar-books-found">No similar books could be found for {book.title}</div>
                                    }
                                </div>   
                            }
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

