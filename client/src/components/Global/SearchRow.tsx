import React from "react";
import { book } from "../../interfaces/BookInterface";

interface SearchRowProps {
    book: book
    customNavigate: (to: string, options?: any) => void
}

export const SearchRow: React.FC<SearchRowProps> = ( { book, customNavigate } ) => {
    return (
            <div className="search-row" onClick={() => customNavigate(`/app/book/${book.id}`, {state: { bookData: book }})} >
                <img className="search-row-image" src={book.imageUrl} />
                <div className="search-row-details">
                    <p className="search-row-title">{book.title}</p>
                    <p className="search-row-author">by {book.authors[0]}</p>
                </div>
            </div>
    );
}