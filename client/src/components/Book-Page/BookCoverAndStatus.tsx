import React from "react";
import { book } from "../../interfaces/BookInterface";
import { PencilIcon } from "./PencilIcon";
import { DownArrowIcon } from "../Global/DownArrowIcon";

interface BookCoverAndStatusProps {
    book: book | null
    bookStatus: string
    handleWantToRead: () => void
    handleChooseShelfButton: () => void
}

export const BookCoverAndStatus: React.FC<BookCoverAndStatusProps> = ({ book, bookStatus, handleWantToRead, handleChooseShelfButton }) => {
    return (
        book ? 
        <div className="book-page-left-column">
            <img className="book-page-book-cover" src={book.imageUrl} alt="Book cover" />
            <div className="reading-status-btn-container">
                <button className={`reading-status-btn ${bookStatus.length > 0 ? 'added-to-shelf' : ''}`} onClick={handleWantToRead}>
                    {
                        bookStatus.length > 0 ? 
                        <>
                            <PencilIcon className="pencil-icon" />
                            {bookStatus}
                        </>
                        :
                        <>
                            Want to read
                        </>
                    }
                </button>
                {
                    bookStatus.length < 1 && 
                    <button className="choose-shelf-btn" onClick={handleChooseShelfButton}>
                        <DownArrowIcon className="choose-shelf-icon" width="15" height="15" />
                    </button>
                }
            </div>
        </div>
        :
        <div>
            No valid book to display the cover and status for.
        </div>
    )
}