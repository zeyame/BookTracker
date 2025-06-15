import React, { useEffect, useState } from "react";
import '../../styles/book-page.css';
import { Link, useLocation } from "react-router-dom";
import { book } from "../../interfaces/BookInterface";
import { SearchBar } from "../../components/Global/SearchBar";
import { RightArrowIcon } from "../../components/Global/RightArrowIcon";
import { sliceDescription } from "../../utils/sliceDescription";
import { ShelfModal } from "../../components/Global/ShelfModal";
import { RemoveFromShelfModal } from "../../components/Global/RemoveFromShelfModal";
import { useShelfModal } from "../../custom-hooks/UseShelfModal";
import { useAuthRedirect } from "../../custom-hooks/useAuthRedirect";
import { useFetchSimilarBooks } from "../../custom-hooks/useFetchSimilarBooks";
import { SimilarBooks } from "../../components/Book-Page/SimilarBooks";
import { AboutAuthor } from "../../components/Book-Page/AboutAuthor";
import { useFetchAuthorDetails } from "../../custom-hooks/useFetchAuthorDetails";
import { BookDescription } from "../../components/Book-Page/BookDescription";
import { BookCoverAndStatus } from "../../components/Book-Page/BookCoverAndStatus";
import { addUserBookByStatus, getBookStatusById } from "../../services/userBookService";
import { useBookStatus } from "../../custom-hooks/useBookStatus";

export const BookPage: React.FC = () => {
    useAuthRedirect();

    const location = useLocation();
    const book: book | null = location.state?.bookData;
    const token = sessionStorage.getItem("token") || "";

    // states
    const [bookShowMoreClicked, setBookShowMoreClicked] = useState<boolean>(false);
    const [bookDescription, setBookDescription] = useState<string>('');
    const [bookStatus, setBookStatus] = useBookStatus(book?.id ?? "");
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
        loading: similarBooksLoading,
        error: similarBooksError,
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
    } = useShelfModal(bookStatus, setBookStatus, setShowPopUp, book, token, fullAuthorDescriptionRef.current);


    // effects
    useEffect(() => {
        if (book) {
            setBookDescription(sliceDescription(book.description));
        }
    }, [book]);


    // functions

    // BOOK DESCRIPTION SECTION HELPER FUNCTIONS

    // handles the show more button of the book description section
    const handleBookShowMore = () => {
        if (book) {
            setBookDescription(book.description);
            setBookShowMoreClicked(true);
        }
    }

    // handles the show less button of the book description section
    const handleBookShowLess = () => {
        if (book) {
            setBookDescription(sliceDescription(bookDescription));
            setBookShowMoreClicked(true);          
        } 
    }

    // BOOK STATUS HANDLER FUNCTIONS

    // handles a click on the defaultly displayed Want To Read button
    const handleWantToRead = async () => {
        if (!book) return;

        // if the reading status button was clicked already, show modal 
        if (bookStatus.length > 0) {
            setShowModal(true);
            return;
        }

        try {
            // persist the book with "Want to read" status
            await addUserBookByStatus(book, "Want to read", token, fullAuthorDescriptionRef.current);
            setBookStatus("Want to read");
            setShowPopUp(true);

            setTimeout(() => {
                setShowPopUp(false);
            }, 3000);
        } catch (error) {
            console.error("Failed to shelf book for the first time:", error);
        }
    }

    // handles clicks for the drop down button next to reading status
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
                <BookCoverAndStatus 
                    book={book} 
                    bookStatus={bookStatus} 
                    handleWantToRead={handleWantToRead} 
                    handleChooseShelfButton={handleChooseShelfButton} 
                />

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
                            <BookDescription 
                                bookDescription={bookDescription} 
                                fullBookDescription={book.description} 
                                bookShowMoreClicked={bookShowMoreClicked} 
                                handleShowMore={handleBookShowMore} 
                                handleShowLess={handleBookShowLess} 
                            />
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
                            {book.pageCount > 0 && 
                                <p className="page-count">Page count: {book.pageCount}</p>  
                            }
                            <p className="published">Published {book.publishedDate} by {book.publisher}</p>
                            <p className="language">Language: {book.language === 'en' ? 'English' : `${book.language}`}</p>
                        </div>

                        <AboutAuthor 
                            book={book} 
                            loading={aboutAuthorLoading} 
                            error={aboutAuthorError} 
                            aboutAuthor={aboutAuthor} 
                            fullAuthorDescriptionRef={fullAuthorDescriptionRef.current} 
                            showMoreButtonClicked={authorShowMoreClicked} 
                            handleShowMore={authorHandleShowMore} 
                            handleShowLess={authorHandleShowLess}  
                        />

                        <SimilarBooks 
                            loading={similarBooksLoading} 
                            error={similarBooksError} 
                            similarBooks={similarBooks} 
                            handleLeftArrowClick={handleLeftArrowClick} 
                            handleRightArrowClick={handleRightArrowClick} 
                            book={book} 
                        />

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

