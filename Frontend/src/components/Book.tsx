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
            <img className="book-image" src={image_url} alt="Book" />
            <p className="title-author"><span className="book-title">{name}</span> - <span className="book-author">{author}</span></p>
        </div>
    )
}