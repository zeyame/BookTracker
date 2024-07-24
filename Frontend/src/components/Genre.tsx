import React, { useState } from "react";
import { Book } from "./Book";
import { book } from "../interfaces/BookInterface";

interface GenreProps {
    name: string
    books: Array<book>
    svgClick: (genreName: string, offset: number) => void
}

export const Genre: React.FC<GenreProps> = ( { name, books, svgClick } ) => {
    const booksLength = books.length;

    // each genre component will maintain an offset saved in session storage
    const getOffset = () => {
        const storedOffset = sessionStorage.getItem(`${name}-offset`);
        return storedOffset !== null ? Number(storedOffset) : 0;
    };

    const saveOffset = (newOffset: number) => {
        sessionStorage.setItem(`${name}-offset`, JSON.stringify(newOffset));
    }

    const currentOffset: number = getOffset();

    return (
        <div className="genre-section">
            <h3 className="genre-title">{name.charAt(0).toUpperCase() + name.slice(1)}</h3>
            <div className="genre-books">
                {books.map((book, index) => 
                    <Book key={book.id} book={book} last={index === booksLength-1 && true} />
                )}

                {/* We only add the pagination svg if books exist in the genre */}
                {booksLength > 0 && 
                    <div className="load-more-icon">
                        <svg fill="#000000" height="100" width="100" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 300" 
                                onClick={() => {
                                    const newOffset = currentOffset + 7;           // 7 is the limit for each batch of books fetched for a given genre
                                    saveOffset(newOffset);
                                    svgClick(name, newOffset);
                                }
                            } >
                            <path d="M250.606,154.389l-150-149.996c-5.857-5.858-15.355-5.858-21.213,0.001
                                c-5.857,5.858-5.857,15.355,0.001,21.213l139.393,139.39L79.393,304.394c-5.857,5.858-5.857,15.355,0.001,21.213
                                C82.322,328.536,86.161,330,90,330s7.678-1.464,10.607-4.394l149.999-150.004c2.814-2.813,4.394-6.628,4.394-10.606
                                C255,161.018,253.42,157.202,250.606,154.389z"/>
                        </svg> 
                    </div>
                }
            </div>
        </div>
    );
}