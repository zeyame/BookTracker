import React from "react"
import '../../styles/book-component.css'
import { book } from "../../interfaces/BookInterface"
import { Link } from "react-router-dom"

interface BookProps {
    book: book
}

export const Book: React.FC<BookProps> = ({ book }) => {
    return (
        <div className="book-container">
            <Link to={`/book/${book.id}`} state={{ bookData: book }}>
                <img className="book-cover" src={book.imageUrl} alt="Book" />
            </Link>
            <div className="book-info">
                <Link to={`/book/${book.id}`} state={{ bookData: book }}>
                    <p className="book-title">{book.title}</p>
                </Link>
                <p className="book-author">{book.authors[0]}</p>
            </div>
        </div>
    );
}