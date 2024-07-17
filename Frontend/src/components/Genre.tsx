import React from "react";
import { Book } from "./Book";

interface GenreProps {
    name: string
    books?: string[]
}

export const Genre: React.FC<GenreProps> = ( { name, books} ) => {
    return (
        <div className="genre-section">
            <h3 className="genre-title">{name}</h3>
            <div id="fiction-genre" className="genre-books">
                <Book />
                <Book />
                <Book />
                <Book />
                <Book />
                <Book />
                <Book />
                <Book />
                <Book />
                <Book />
            </div>
        </div>
    );
}