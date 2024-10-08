import React from "react";
import { book } from "../../interfaces/BookInterface";
import { Link } from "react-router-dom";
import { LeftArrowIcon } from "../Global/LeftArrowIcon";
import { RightArrowIcon } from "../Global/RightArrowIcon";

interface SimilarBookProps {
    book: book
    isLast: boolean
    handleLeftArrowClick: () => void
    handleRightArrowClick: () => void
}


export const SimilarBook: React.FC<SimilarBookProps> = ({ book, isLast, handleLeftArrowClick, handleRightArrowClick }) => {
    return (
        <>
            <div className="similar-book">
                <Link to={`/app/book/${book.id}`} state={ {bookData: book}}>
                    <img src={book.imageUrl} alt="Similar book to original book." className="similar-book-cover" />
                </Link>
                <div className="similar-book-info">
                    <Link to={`/app/book/${book.id}`} state={ {bookData: book}}>
                        <p className="similar-book-title">{book.title}</p>
                    </Link>
                    {
                        book.authors.map(author => 
                            <p key={author} className="similar-book-author">{author}</p>
                        )
                    }
                </div>
            </div>
            {   
                isLast && 
                    <div className="similar-books-pagination-arrows">
                        <LeftArrowIcon leftArrowClicked={handleLeftArrowClick} />
                        <RightArrowIcon className="similar-books-right-arrow-pagination" rightArrowClicked={handleRightArrowClick} />
                    </div>
            }
        </>
    );
}