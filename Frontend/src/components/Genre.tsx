import React from "react";
import { Book } from "./Book";
import { book } from "../BookInterface";
import {v4 as uuidv4} from 'uuid';

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
                    <Book key={uuidv4()} name={book.name} image_url={book.image_url} />
                )}
            </div>
        </div>
    );
}