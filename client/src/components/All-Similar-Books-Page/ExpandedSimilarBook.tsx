import React, { useEffect, useRef, useState } from "react";
import { book } from "../../interfaces/BookInterface";
import { sliceDescription } from "../../utils/sliceDescription";
import { Link } from "react-router-dom";
import { getStoredBookStatus } from "../../utils/getStoredBookStatus";
import { useShelfModal } from "../../custom-hooks/UseShelfModal";
import { ShelfModal } from "../Global/ShelfModal";
import { RemoveFromShelfModal } from "../Global/RemoveFromShelfModal";
import { BookWithStatus } from "../../interfaces/BookWithStatus";

interface ExpandedSimilarBookProps {
    similarBook: book
}

export const ExpandedSimilarBook: React.FC<ExpandedSimilarBookProps> = ({ similarBook }) => {

    // states
    const [bookDescription, setBookDescription] = useState<string>('');
    const [showMoreButtonClicked, setShowMoreButtonClicked] = useState<boolean>(false);
    const [bookStatus, setBookStatus] = useState<string>(getStoredBookStatus(similarBook));
    const [showPopUp, setShowPopUp] = useState<boolean>(false);

    // saving book status to local storage
    useEffect(() => {
        if (similarBook && bookStatus) {
  
            const storedBooksWithStatus: string | null = localStorage.getItem("booksWithStatus");
  
            let books: Record<string, BookWithStatus> = storedBooksWithStatus ? JSON.parse(storedBooksWithStatus) : {};

            books[similarBook.id] = {bookData: similarBook, status: bookStatus}
  
            localStorage.setItem("booksWithStatus", JSON.stringify(books));
        }
  
    }, [similarBook, bookStatus]);

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

    // refs
    const fullBookDescriptionRef = useRef<string>(similarBook.description);

    // effects
    useEffect(() => {
        setBookDescription(sliceDescription(similarBook.description));
    }, [similarBook]);

    // functions
    const handleShowMore = () => {
        setBookDescription(fullBookDescriptionRef.current);
        setShowMoreButtonClicked(true);
    };

    const handleShowLess = () => {
        setBookDescription(sliceDescription(fullBookDescriptionRef.current));
        setShowMoreButtonClicked(false);
    }

    return (
        <div className="expanded-similar-book">
            {
                showPopUp && (
                    <div className="shelf-added-popup">
                        <span>Shelved as "{bookStatus}"</span>
                    </div>
                )
            }

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
                    handleRemoveFromShelfButton={() => handleRemoveFromShelfButton(similarBook)}
                /> 
            }
            
            <Link to={`/app/book/${similarBook.id}`} state={ {bookData: similarBook} }>
                <img className="expanded-similar-book-cover" src={similarBook.imageUrl} alt="Book cover" />    
            </Link>
            <div className="expanded-similar-book-details">
                <Link to={`/app/book/${similarBook.id}`} state={ {bookData: similarBook} }>
                    <h3 className="expanded-similar-book-title">
                        {similarBook.title}
                    </h3>
                </Link>
                <div className="expanded-similar-book-authors">
                    by {similarBook.authors.map((author, index) => 
                        <p key={author} className="expanded-similar-book-author">
                            {index === 0 ? author : ', ' + author}
                        </p>
                    )}
                </div>
                {
                    bookDescription.length < fullBookDescriptionRef.current.length ? 
                    <>
                        <p className="expanded-similar-book-description-faded">
                            {bookDescription}
                        </p>
                        
                        <div>
                            <button className="expanded-similar-book-show-btn" onClick={handleShowMore}>Show more</button>
                            <svg className="arrow-down-icon" height='10' width='10' xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" onClick={handleShowMore}>
                                <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 13.5 12 21m0 0-7.5-7.5M12 21V3" />
                            </svg>
                        </div>
                    
                    </>
                    :
                        <>
                            <p className="expanded-similar-book-description">
                                {bookDescription}
                            </p>
                            {
                                showMoreButtonClicked && 
                                <div>
                                    <button className="expanded-similar-book-show-btn" onClick={handleShowLess}>Show less</button>
                                    <svg className="arrow-down-icon" height='10' width='10' xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" onClick={handleShowLess}>
                                        <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 13.5 12 21m0 0-7.5-7.5M12 21V3" />
                                    </svg>
                                </div>
                            }
                        </>
                }
                <button className="expanded-similar-book-reading-status-btn" onClick={() => setShowModal(true)}>
                    {bookStatus.length > 0 ? bookStatus : "Shelf book"}
                </button>
            </div>
        </div>
    );
}