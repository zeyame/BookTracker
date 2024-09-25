import React from "react";
import { LoadingIcon } from "../Global/LoadingIcon";
import { book } from "../../interfaces/BookInterface";
import { SimilarBook } from "./SimilarBook";

interface SimilarBooksProps {
    loading: boolean
    error: boolean
    similarBooks: Array<book>
    handleLeftArrowClick: () => void
    handleRightArrowClick: () => void
    book: book | null
}

export const SimilarBooks: React.FC<SimilarBooksProps> = ({loading, error, similarBooks, handleLeftArrowClick, handleRightArrowClick, book}) => {
    return (
        
        <div className="similar-books-container">
            <div className="similar-books-header-div">
                <p className="similar-books-header">
                    Readers also enjoyed
                </p>
            </div>
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
                        similarBooks && similarBooks.length > 0 ?
                            similarBooks.map((book, index) => 
                                book && <SimilarBook key={book.id} book={book} isLast={index === similarBooks.length-1} handleLeftArrowClick={handleLeftArrowClick} handleRightArrowClick={handleRightArrowClick} />
                            )
                        :
                        <div className="no-similar-books-found">No similar books could be found for {book ? book.title : "this book"}</div>
                    }
                </div>   
            }
        </div>
    )
}