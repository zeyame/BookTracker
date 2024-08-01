import React, { useEffect, useRef, useState } from "react";
import { useLocation } from "react-router-dom";
import { book } from "../interfaces/BookInterface";
import { SearchBar } from "../components/SearchBar";
import '../styles/book-page.css';
import { LoadingIcon } from "../components/LoadingIcon";
import { fetchAuthorDescription } from "../services/authorSearch";

export const BookPage: React.FC = () => {

    const location = useLocation();
    const book: book | undefined = location.state.bookData;
    

    // states
    const [aboutAuthor, setAboutAuthor] = useState<string>('');
    const [fetchingAboutAuthor, setFetchingAboutAuthor] = useState<boolean>(false);
    const [aboutAuthorError, setAboutAuthorError] = useState<boolean>(false);
    const [sliced, setSliced] = useState<boolean>(false);

    // refs
    const fullAuthorDescription = useRef<string>('');


    // effects
    // fetches description about the author of the book
    useEffect(() => {
        if (book) {
            if (sessionStorage.getItem(`author-${book.authors[0]}`)) {
                const aboutAuthorObject: string | null = sessionStorage.getItem(`author-${book.authors[0]}`);
                if (aboutAuthorObject) {
                    const storedAuthorDescription: string = JSON.parse(aboutAuthorObject);
                    fullAuthorDescription.current = storedAuthorDescription;
                    const slicedDescription = sliceAuthorDescription(storedAuthorDescription);
                    setAboutAuthor(slicedDescription);
                    
                    // Slicing was 'useful'
                    slicedDescription.length < storedAuthorDescription.length && setSliced(true);
                    
                }
            }
            else {
                setFetchingAboutAuthor(true);
                const getAboutAuthor = async () => {
                    try {
                        const authorDescription: string = await fetchAuthorDescription(book.authors[0]);
                        fullAuthorDescription.current = authorDescription;
                        const slicedDescription = sliceAuthorDescription(authorDescription);
                        setAboutAuthor(slicedDescription);

                        // Slicing was 'useful'
                        slicedDescription.length < authorDescription.length && setSliced(true);
                    }
                    catch (error: any) {
                        setAboutAuthorError(true);
                    }
                    finally {
                        setFetchingAboutAuthor(false);
                    }
                }
                getAboutAuthor();
            }
        }

        return () => {
            setAboutAuthor('');
        }
    }, []);

    // stores author description in session storage
    useEffect(() => {
        if (aboutAuthor) {
            sessionStorage.setItem(`author-${book?.authors[0]}`, JSON.stringify(fullAuthorDescription.current));
        }
    }, [aboutAuthor]);


    // functions
    const sliceAuthorDescription = (description: string): string => {
        // Split the description into sentences using regex to account for various sentence endings
        const sentences = description.split(/(?<=[.!?])\s+/);
    
        // Slice the array to get the first 5 sentences
        const slicedSentences = sentences.slice(0, 5);
    
        // Join the sentences back into a single string
        return slicedSentences.join(' ');
    }

    const handleShowMore = () => {
        setAboutAuthor(fullAuthorDescription.current);
    }
    

    if (!book) {
        return (
            <div>Book Not Found.</div>
        );
    }

    return (
        <div className="book-page-container">
            <SearchBar />
            <div className="book-page-main">
                <div className="book-page-left-column">
                    <img className="book-page-book-cover" src={book.image_url} alt="Book cover" />
                    <button className="reading-status-btn">Want to read</button>
                    <button className="buy-amazon-btn">Buy on Amazon</button>
                </div>
                <div className="book-page-main-content">
                    <div className="book-page-title-section">
                        <h1 className="book-title-header">{book.title}</h1>
                    </div>
                    <div className="book-page-metadata-section">
                        <div className="book-page-authors">
                            {book.authors.map((author, index) => 
                                <h3 className="book-page-author" key={author}>{index > 0 && ', '}{author}</h3>
                            )}
                        </div>
                        <div className="book-page-description">
                            <p>{book.description}</p>
                        </div>
                        {book.categories.length > 0 &&
                            <div className="book-page-genres">
                                <p>Genres:</p> 
                                {book.categories.map(category =>
                                    <p className="book-page-genre" key={category}>{category}</p>
                                )}
                            </div>
                        }
                        <div className="edition-details">
                            <p className="edition-details-title">This edition</p>
                            <p className="page-count">Page count: {book.pageCount}</p>
                            <p className="published">Published {book.publishedDate} by {book.publisher}</p>
                            <p className="language">Language: {book.language === 'en' ? 'English' : `${book.language}`}</p>
                        </div>
                        <div className="about-author-container">
                            <hr className="about-author-divider" />
                            <p className="about-author-header">
                                About the author
                            </p>
                            {
                                fetchingAboutAuthor ? 
                                    <div className="loading-about-author">
                                        <p>Loading</p>
                                        <LoadingIcon />
                                    </div>
                                : 
                                aboutAuthorError ?
                                    <div className="about-author-error">
                                        <p className="about-author-error-message">Failed to fetch author's description. Please refresh to try again.</p>
                                    </div>
                                :
                                <div>
                                    {
                                        aboutAuthor.length < fullAuthorDescription.current.length ?
                                        <>
                                            <p className= "author-description-faded" >
                                                {aboutAuthor}
                                            </p>

                                            <button className="show-more-btn" onClick={handleShowMore}>Show more</button>
                                            <svg className="arrow-down-icon" height='10' width='10' xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" >
                                                <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 13.5 12 21m0 0-7.5-7.5M12 21V3" />
                                            </svg>
                                        </>
                                        :
                                        <>
                                            <p className= "author-description" >
                                                {aboutAuthor}
                                            </p>
                                            {
                                                sliced &&
                                                <>
                                                    <button className="show-more-btn">Show less</button>
                                                    <svg className="arrow-down-icon" height='10' width='10' xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" >
                                                        <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 13.5 12 21m0 0-7.5-7.5M12 21V3" />
                                                    </svg>
                                                </>
                                            }
                                        </>
                                    }
                                    <hr className="about-author-divider"/>
                                </div>
                            }
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

