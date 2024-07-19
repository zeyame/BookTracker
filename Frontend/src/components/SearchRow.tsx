import React from "react";
import { book } from "../interfaces/BookInterface";

interface SearchRowProps {
    book: book
}

export const SearchRow: React.FC<SearchRowProps> = ( {book} ) => {
    return (
        <div className="search-row">
            <img className="search-row-image" src={book.image_url} />
            <div className="search-row-details">
                <p className="search-row-title">{book.name}</p>
                <p className="search-row-author">by {book.author}</p>
            </div>
        </div>
    )
}