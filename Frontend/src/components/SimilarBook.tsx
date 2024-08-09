import React from "react";
import { book } from "../interfaces/BookInterface";
import { Link } from "react-router-dom";

interface SimilarBookProps {
    book: book
}


export const SimilarBook: React.FC<SimilarBookProps> = ({ book }) => {
    return (
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
    );
}