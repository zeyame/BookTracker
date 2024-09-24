import React, { useEffect, useRef, useState } from "react";
import { BookWithStatus } from "../../interfaces/BookWithStatus";
import "../../styles/book-page-by-status.css";
import { Link } from "react-router-dom";
import { sliceDescriptionBySentences } from "../../utils/sliceDescription";
import { useShelfModal } from "../../custom-hooks/UseShelfModal";
import { ShelfModal } from "./ShelfModal";
import { RemoveFromShelfModal } from "./RemoveFromShelfModal";

interface BookPageByStatusProps {
    book: BookWithStatus;
}

export const BookPageByStatus: React.FC<BookPageByStatusProps> = ({ book }) => {
    const [bookStatus, setBookStatus] = useState<string>(book.status);
    const [showPopUp, setShowPopUp] = useState<boolean>(false);
    
    const {
        showModal,
        setShowModal,
        selectedShelf,
        showRemoveFromShelfModal,
        handleExitModal,
        handleModalWantToRead,
        handleCurrentlyReading,
        handleRead,
        handleRemoveFromShelf,
        handleDone,
        handleExitRemoveFromShelfModal,
        handleRemoveFromShelfButton
    } = useShelfModal(bookStatus, setBookStatus, setShowPopUp);
    
    const [numberOfSentences, setNumberOfSentences] = useState<number>(0);

    const getNumberOfSentences = (description: string): number => {
        const sentences: Array<string> = description.match(/[^.!?]+[.!?]+/g) || [];
        return sentences.length;
    };

    useEffect(() => {
        if (book) {
            const sentenceCount = getNumberOfSentences(book.bookData.description);
            setNumberOfSentences(sentenceCount);
        }
    }, [book]);

    let numberOfDivisions: number = numberOfSentences <= 3 ? 2 : 3;

    return (
        <div className="book-page-by-status-container">
            {showPopUp && (
                <div className="shelf-added-popup">
                    <span>Shelved as "{bookStatus}"</span>
                </div>
            )}

            {showModal && 
                <ShelfModal 
                    handleExitModal={handleExitModal} 
                    handleCurrentlyReading={handleCurrentlyReading} 
                    handleModalWantToRead={handleModalWantToRead} 
                    handleRead={handleRead} 
                    handleRemoveFromShelf={handleRemoveFromShelf} 
                    handleDone={handleDone} 
                    selectedShelf={selectedShelf} 
                />
            }
            
            {showRemoveFromShelfModal && 
                <RemoveFromShelfModal 
                    handleExitRemoveFromShelfModal={handleExitRemoveFromShelfModal} 
                    handleCancelRemoveFromShelf={handleExitRemoveFromShelfModal} 
                    handleRemoveFromShelfButton={() => handleRemoveFromShelfButton(book.bookData)}
                /> 
            }

            
            <div className="book-page-by-status-main">
                <div className="book-page-by-status-header">
                    <img src={book.bookData.imageUrl} className="book-page-by-status-cover" alt="Book cover" />
                    <div className="book-page-by-status-title-author-container">
                        <h2 className="book-page-by-status-title">
                            {book.bookData.title}
                        </h2>
                        <Link to={`/author/${book.bookData.authors[0]}`}>
                            <p className="book-page-by-status-author">
                                {book.bookData.authors[0]}
                            </p>
                        </Link>
                        <button className="book-page-by-status-reading-status-button" onClick={() => setShowModal(true)}>
                            {bookStatus.length > 0 ? bookStatus : "Shelf book"}
                        </button>
                    </div>
                </div>
                {book.bookData.description.length > 0 && (
                    <div className="book-page-by-status-overview">
                        <h3 style={{ margin: "0px" }}>Overview</h3>
                        <div className="book-page-by-status-book-description">
                            <p>
                                {sliceDescriptionBySentences(book.bookData.description, 0, Math.floor(numberOfSentences / numberOfDivisions))}
                            </p>
                            <p>
                                {sliceDescriptionBySentences(book.bookData.description, Math.floor(numberOfSentences / numberOfDivisions), Math.floor((numberOfSentences * 2) / numberOfDivisions))}
                            </p>
                            <p>
                                {sliceDescriptionBySentences(book.bookData.description, Math.floor((2 * numberOfSentences) / numberOfDivisions), numberOfSentences - Math.floor((2 * numberOfSentences) / numberOfDivisions))}
                            </p>
                        </div>
                        {
                            book.authorDescription && 
                            <div className="book-page-by-status-author-description">
                                {book.authorDescription}
                            </div>
                        }
                    </div>
                )}
            </div>
        </div>
    );
};
