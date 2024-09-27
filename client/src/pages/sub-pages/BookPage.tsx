import React, { useEffect, useMemo, useRef, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { book } from "../../interfaces/BookInterface";
import { SearchBar } from "../../components/Global/SearchBar";
import '../../styles/book-page.css';
import { LoadingIcon } from "../../components/Global/LoadingIcon";
import { Author } from "../../interfaces/AuthorInterface";
import { fetchAuthorDetails } from "../../services/authorSearch";
import { RightArrowIcon } from "../../components/Global/RightArrowIcon";
import { sliceDescription } from "../../utils/sliceDescription";
import { DownArrowIcon } from "../../components/Global/DownArrowIcon";
import { PencilIcon } from "../../components/Book-Page/PencilIcon";
import { ShelfModal } from "../../components/Global/ShelfModal";
import { RemoveFromShelfModal } from "../../components/Global/RemoveFromShelfModal";
import { BookWithStatus } from "../../interfaces/BookWithStatus";
import { useShelfModal } from "../../custom-hooks/UseShelfModal";
import { getStoredBookStatus } from "../../utils/getStoredBookStatus";
import { useAuthRedirect } from "../../utils/useCheckForToken";
import { useFetchSimilarBooks } from "../../custom-hooks/useFetchSimilarBooks";
import { SimilarBooks } from "../../components/Book-Page/SimilarBooks";
import { AboutAuthor } from "../../components/Book-Page/AboutAuthor";
import { useFetchAuthorDetails } from "../../custom-hooks/useFetchAuthorDetails";

export const BookPage: React.FC = () => {
    useAuthRedirect();

    const location = useLocation();
    const book: book | null = location.state?.bookData;

    // states
    const [bookShowMoreClicked, setBookShowMoreClicked] = useState<boolean>(false);
    const [bookDescription, setBookDescription] = useState<string>('');
    const [bookStatus, setBookStatus] = useState<string>(getStoredBookStatus(book));
    const [showPopUp, setShowPopUp] = useState<boolean>(false);

    // useFetchAuthorDetails custom hook handles all fetching and handling logic for the AboutAuthor section/component
    const {
        loading: aboutAuthorLoading,
        error: aboutAuthorError,
        aboutAuthor,
        showMoreButtonClicked: authorShowMoreClicked,
        fullAuthorDescriptionRef,
        handleShowMore: authorHandleShowMore,
        handleShowLess: authorHandleShowLess,
    } = useFetchAuthorDetails(book);

    // useFetchSimilarBooks custom hook handles all fetching and handling logic for the similar books
    const {
        similarBooks,
        similarBooksLoading,
        similarBooksError,
        handleLeftArrowClick,
        handleRightArrowClick,
        allSimilarBooksRef
    } = useFetchSimilarBooks(book);

    // useShelfModal custom hook encompasses all logic for both the shelf modal and the remove from shelf modal components 
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


    // effects


    useEffect(() => {
        if (book) {
            setBookDescription(sliceDescription(book.description));
            setBookStatus(getStoredBookStatus(book));
        }

        return () => {
            setBookDescription('');
            setBookStatus('');
        }
    }, [book]);


    // saving book status to local storage
    useEffect(() => {
        if (book && bookStatus) {

            const storedBooksWithStatus: string | null = localStorage.getItem("booksWithStatus");

            let books: Record<string, BookWithStatus> = storedBooksWithStatus ? JSON.parse(storedBooksWithStatus) : {};

            books[book.id] = {bookData: book, status: bookStatus, authorDescription: fullAuthorDescriptionRef.current};

            localStorage.setItem("booksWithStatus", JSON.stringify(books));

        }

    }, [book, bookStatus]);


    // functions


    // BOOK AND AUTHOR DETAILS SECTION FUNCTIONS

    // handles the show more button of book and author descriptions
    const handleShowMore = (descriptionType: string) => {
        if (descriptionType.toLowerCase().replace('/\s+/g', '') === 'bookdescription') {
            if (book) {
                setBookDescription(book.description);
                setBookShowMoreClicked(true);
            }
        }
        else {
            console.log("Parameter given to handleShowMore function must either be 'about author' or 'book description'.");
        }
    }

    // handles the show less button of book and author descriptions
    const handleShowLess = (descriptionType: string) => {
        if (descriptionType.toLowerCase().replace('/\s+/g', '') === 'bookdescription') {
            if (book) {
                setBookDescription(sliceDescription(bookDescription));
                setBookShowMoreClicked(true);          
            } 
        }
        else {
            console.log("Parameter given to handleShowLess function must either be 'about author' or 'book description'.");
        }
    }


    // BOOK BUTTON HANDLER FUNCTIONS
    const handleWantToRead = () => {

        // if the reading status button was clicked already, show modal 
        if (bookStatus.length > 0) {
            setShowModal(true);
            return;
        }

        setBookStatus("Want to read");
        setShowPopUp(true);

        // Hide the popup after 3 seconds
        setTimeout(() => {
            setShowPopUp(false);
        }, 3000);
    }

    const handleChooseShelfButton = () => {
        setShowModal(true);
    }


    if (!book) {
        return (
            <div>Book Not Found.</div>
        );
    }

    return (
        <div className="book-page-container">
            <SearchBar />
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
                    handleRemoveFromShelfButton={() => handleRemoveFromShelfButton(book)}
                /> 
            }

            <div className="book-page-main">
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
                <div className="book-page-main-content">
                    <div className="book-page-title-section">
                        <h1 className="book-title-header">{book.title}</h1>
                    </div>
                    <div className="book-page-metadata-section">
                        <div className="book-page-authors">
                            {book.authors.map((author, index) => 
                                <h3 key={author} className="book-page-author" >{index > 0 && ', '}{author}</h3>
                            )}
                        </div>
                            { bookDescription &&
                                <div className="book-page-description">
                                    { bookDescription.length < book.description.length ?
                                        <>
                                            <p className= "description-faded" >
                                                {bookDescription}
                                            </p>

                                            <button className="show-more-btn" onClick={() => handleShowMore('bookDescription')}>Show more</button>
                                            <svg className="arrow-down-icon" height='10' width='10' xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" onClick={() => handleShowMore('bookDescription')} >
                                                <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 13.5 12 21m0 0-7.5-7.5M12 21V3" />
                                            </svg>
                                        </>
                                        :
                                        <>
                                            <p className= "description" >
                                                {bookDescription}
                                            </p>
                                            {
                                                bookShowMoreClicked &&
                                                <>
                                                    <button className="show-more-btn" onClick={() => handleShowLess('bookDescription')}>Show less</button>
                                                    <svg className="arrow-down-icon" height='10' width='10' xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" onClick={() => handleShowLess('bookDescription')} >
                                                        <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 13.5 12 21m0 0-7.5-7.5M12 21V3" />
                                                    </svg>
                                                </>
                                            }
                                        </>
                                    }
                                </div>
                            }                        
                        {book.categories.length > 0 &&
                            <div className="book-page-genres">
                                <p>Genres:</p> 
                                {book.categories.map(category =>
                                    <p key={category} className="book-page-genre" >{category}</p>
                                )}
                            </div>
                        }
                        <div className="edition-details">
                            <p className="edition-details-title">This edition</p>
                            <p className="page-count">Page count: {book.pageCount}</p>
                            <p className="published">Published {book.publishedDate} by {book.publisher}</p>
                            <p className="language">Language: {book.language === 'en' ? 'English' : `${book.language}`}</p>
                        </div>
                        <AboutAuthor book={book} loading={aboutAuthorLoading} error={aboutAuthorError} aboutAuthor={aboutAuthor} fullAuthorDescriptionRef={fullAuthorDescriptionRef.current} showMoreButtonClicked={authorShowMoreClicked} handleShowMore={authorHandleShowMore} handleShowLess={authorHandleShowLess}  />
                        <SimilarBooks loading={similarBooksLoading} error={similarBooksError} similarBooks={similarBooks} handleLeftArrowClick={handleLeftArrowClick} handleRightArrowClick={handleRightArrowClick} book={book} />
                        <Link to={`/app/similar-books/${book.id}`} state={ { originalBook: book, similarBooks: allSimilarBooksRef.current } }>
                            <div className="all-similar-books-btn-container">
                                <button className="all-similar-books-btn">All similar books</button>
                                <RightArrowIcon height="20" width="20" className="all-similar-books-btn-svg" />
                            </div>
                        </Link>
                        <hr className="similar-books-divider" />
                    </div>
                </div>
            </div>
        </div>
    );
}

