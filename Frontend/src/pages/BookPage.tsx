import React, { useEffect, useMemo, useRef, useState } from "react";
import { useLocation } from "react-router-dom";
import { book } from "../interfaces/BookInterface";
import { SearchBar } from "../components/SearchBar";
import '../styles/book-page.css';
import { LoadingIcon } from "../components/LoadingIcon";
import { fetchAuthorDetails } from "../services/authorSearch";
import { Author } from "../interfaces/AuthorInterface";

export const BookPage: React.FC = () => {

    const location = useLocation();
    const book: book | undefined = location.state.bookData;
    

    // states
    const [aboutAuthor, setAboutAuthor] = useState<Author>({
        'description': '',
        'image_url': ''
    });
    const [fetchingAboutAuthor, setFetchingAboutAuthor] = useState<boolean>(false);
    const [aboutAuthorError, setAboutAuthorError] = useState<boolean>(false);
    const [showMoreButtonClicked, setShowMoreButtonClicked] = useState<boolean>(false);

    // refs 
    const fullAuthorDescription = useRef<string>('');

    // effects
    // fetches description about the author of the book
    useEffect(() => {
        const fetchDescription = async () => {
            try {
                if (book) {
                    const storedAuthorDetails = sessionStorage.getItem(`author-${book.authors[0]}`);
                    if (storedAuthorDetails) {
                        const parsedDetails: Author = JSON.parse(storedAuthorDetails);
                        const storedDescription = parsedDetails.description;
                        const storedImage = parsedDetails.image_url;
                        fullAuthorDescription.current = storedDescription;
                        setAboutAuthor(prevState => ({
                            ...prevState,
                            'description': sliceAuthorDescription(storedDescription),
                            'image_url': storedImage
                        }));
                    } 
                    else {
                        setFetchingAboutAuthor(true);
                        const authorDetails: Author | null = await fetchAuthorDetails(book.authors[0]);

                        // if fetching author details did not return empty, we get the description
                        if (authorDetails) {
                            const authorDescription = authorDetails.description;
                            const image_url = authorDetails.image_url;
                            fullAuthorDescription.current = authorDescription;

                            setAboutAuthor(prevState => ({
                                ...prevState,
                                description: sliceAuthorDescription(authorDescription),
                                image_url: image_url
                            }));
                            sessionStorage.setItem(`author-${book.authors[0]}`, JSON.stringify({
                                'description': authorDescription,
                                'image_url': image_url
                            }));
                        }
                    }
                }
            } catch (error) {
                setAboutAuthorError(true);
            } finally {
                setFetchingAboutAuthor(false);
            }
        };
    
        fetchDescription();
    
        return () => {
            setAboutAuthor(prevState => ({
                ...prevState,
                'description': '',
                'image_url': ''
            }));
        };
    }, [book]);
    

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
        setAboutAuthor(prevState => ({
            ...prevState,
            'description': fullAuthorDescription.current
        }));
        setShowMoreButtonClicked(true);
    }

    const handleShowLess = () => {
        const currentDescription = aboutAuthor.description;
        const slicedDescription = sliceAuthorDescription(currentDescription);
        setAboutAuthor(prevState => ({
            ...prevState,
            'description': slicedDescription
        }));
        setShowMoreButtonClicked(false);
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
                                    <div className="book-page-author-details">
                                        <img className="book-page-author-picture" src={aboutAuthor.image_url} />
                                        <p className="book-page-author-name">{book.authors[0]}</p>
                                    </div>
                                    {
                                        aboutAuthor.description.length < fullAuthorDescription.current.length ?
                                        <>
                                            <p className= "author-description-faded" >
                                                {aboutAuthor.description}
                                            </p>

                                            <button className="show-more-btn" onClick={handleShowMore}>Show more</button>
                                            <svg className="arrow-down-icon" height='10' width='10' xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" >
                                                <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 13.5 12 21m0 0-7.5-7.5M12 21V3" />
                                            </svg>
                                        </>
                                        :
                                        <>
                                            <p className= "author-description" >
                                                {aboutAuthor.description}
                                            </p>
                                            {
                                                showMoreButtonClicked &&
                                                <>
                                                    <button className="show-more-btn" onClick={handleShowLess}>Show less</button>
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

