import React from "react";

interface GenreProps {
    name: string
}

export const Genre: React.FC<GenreProps> = ( {name} ) => {
    return (
        <div className="genre-section">
            <h3 className="genre-title">{name}</h3>
            <div id="fiction-genre" className="genre-books">
                <div className="book">
                    Book1
                </div>
                <div className="book">
                    Book2
                </div>
                <div className="book">
                    Book3
                </div>
                <div className="book">
                    Book4
                </div>
                <div className="book">
                    Book5
                </div>
                <div className="book">
                    Book6
                </div>
                <div className="book">
                    Book7
                </div>
                <div className="book">
                    Book8
                </div>
            </div>
        </div>
    );
}