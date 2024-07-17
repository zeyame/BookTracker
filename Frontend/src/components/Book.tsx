import React from "react"
import '../styles/book.css'

export const Book: React.FC = () => {
    return (
        <div className="book-container">
            <img className="book-image" src={require('../assets/book.webp')} alt="Book" />
            <p className="title-author"><span className="book-title">SPQR</span> - <span className="book-author">Mary Beard</span></p>
        </div>
    )
}