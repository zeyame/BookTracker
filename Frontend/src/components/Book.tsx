import React from "react"
import '../styles/book.css'
import { book } from "../interfaces/BookInterface"

interface BookProps {
    book: book
}

export const Book: React.FC<BookProps> = ({ book }) => {
    return (
        <div className="book-container">
            <img className="book-cover" src={book.image_url} alt="Book" />
            <div className="book-info">
                <p className="book-title">{book.name}</p>
                <p className="book-author">{book.author}</p>
            </div>
        </div>
    )
}