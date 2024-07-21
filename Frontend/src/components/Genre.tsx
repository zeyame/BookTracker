import React from "react";
import { Book } from "./Book";
import { book } from "../interfaces/BookInterface";

interface GenreProps {
    name: string
    books: Array<book>
}

export const Genre: React.FC<GenreProps> = ( { name, books } ) => {
    return (
        <div className="genre-section">
            <h3 className="genre-title">{name}</h3>
            <div id="fiction-genre" className="genre-books">
                {books.map(book => 
                    <Book key={book.id} book={book} />
                )}
            </div>
        </div>
    );
}