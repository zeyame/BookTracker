import React from "react";
import { book } from "../../interfaces/BookInterface";
import { Link } from "react-router-dom";
import { LeftArrowIcon } from "../Global/LeftArrowIcon";
import { RightArrowIcon } from "../Global/RightArrowIcon";

interface SimilarBookProps {
    book: book
    isLast: boolean
}


export const SimilarBook: React.FC<SimilarBookProps> = ({ book, isLast }) => {
    return (
        <>
            <div className="similar-book">
                <Link to={`/book/${book.id}`} state={ {bookData: book}}>
                    <img src={book.image_url} alt="Similar book to original book." className="similar-book-cover" />
                </Link>
                <div className="similar-book-info">
                    <Link to={`/book/${book.id}`} state={ {bookData: book}}>
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
                        <LeftArrowIcon />
                        <RightArrowIcon />
                    </div>
            }
        </>
    );
}