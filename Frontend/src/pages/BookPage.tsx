import React from "react";
import { useLocation, useParams } from "react-router-dom";
import { book } from "../interfaces/BookInterface";


export const BookPage: React.FC = () => {

    const location = useLocation();
    const book: book | undefined = location.state.bookData;

    if (!book) {
        return (
            <div>Book Not Found.</div>
        );
    }

    return (
        <div>
            Book title: {book.name}
            Book author: {book.author}
            <img src={`${book.image_url}`} alt="Book cover" />
        </div>
    );
}