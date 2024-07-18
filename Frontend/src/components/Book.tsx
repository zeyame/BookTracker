import React from "react"
import '../styles/book.css'

interface BookProps {
    name: string
    image_url: string
    author: string
}

export const Book: React.FC<BookProps> = ({ name, image_url, author }) => {
    return (
        <div className="book-container">
            <img className="book-cover" src={image_url} alt="Book" />
            <div className="book-info">
                <p className="book-title">{name}</p>
                <p className="book-author">{author}</p>
            </div>
        </div>
    )
}