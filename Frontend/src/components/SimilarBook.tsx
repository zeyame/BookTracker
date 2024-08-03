import React from "react";
import { book } from "../interfaces/BookInterface";

interface SimilarBookProps {
    book: book
}


export const SimilarBook: React.FC<SimilarBookProps> = ({ book }) => {
    return (
        <div className="similar-book">
            <img src={book.image_url} alt="Similar book to original book." className="similar-book-cover" />
            <p className="similar-book-title">{book.title}</p>
            <div className="similar-book-authors">
                {
                    book.authors.map(author => 
                        <p key={author} className="similar-book-author">{author}</p>
                    )
                }
            </div>
        </div>
    );
}