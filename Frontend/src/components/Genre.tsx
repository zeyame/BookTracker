import React, { useState } from "react";
import { Book } from "./Book";
import { book } from "../interfaces/BookInterface";
import { LoadingIcon } from "./LoadingIcon";

interface GenreProps {
    name: string
    books: Array<book>
    svgClick: (genreName: string) => void
    loading: boolean
}

export const Genre: React.FC<GenreProps> = ( { name, books, svgClick, loading } ) => {
    const booksLength = books.length;

    return (
        <div className="genre-section">
            <h3 className="genre-title">{name.charAt(0).toUpperCase() + name.slice(1)}</h3>
            <div className="genre-books">
                {books.map(book => 
                    <Book key={book.id} book={book} />
                )}

                {/* We only add the pagination svg if books exist in the genre */}
                {booksLength > 0 && 
                    loading ? 
                    <div className="load-more-or-loading">
                        <LoadingIcon /> 
                    </div>
                    :
                    <div className="load-more-or-loading">
                        <svg fill="#000000" height="100" width="100" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 300" onClick={() => svgClick(name)}>
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