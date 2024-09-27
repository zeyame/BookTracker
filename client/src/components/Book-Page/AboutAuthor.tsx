import React from "react";
import { Author } from "../../interfaces/AuthorInterface";
import { LoadingIcon } from "../Global/LoadingIcon";
import { book } from "../../interfaces/BookInterface";

interface AboutAuthorProps {
    book: book | null
    loading: boolean
    error: boolean
    aboutAuthor: Author
    fullAuthorDescriptionRef: string
    showMoreButtonClicked: boolean
    handleShowMore : () => void
    handleShowLess : () => void
}

export const AboutAuthor: React.FC<AboutAuthorProps> = ({book, loading, error, aboutAuthor, fullAuthorDescriptionRef, showMoreButtonClicked, handleShowMore, handleShowLess }) => {
    return (
        <div className="about-author-container">
            <hr className="about-author-divider" />
            <p className="about-author-header">
                About the author
            </p>
            {
                loading ? 
                    <div className="loading-about-author">
                        <p>Loading</p>
                        <LoadingIcon />
                    </div>
                : 
                error ?
                    <div className="about-author-error">
                        <p className="about-author-error-message">Failed to fetch author's description. Please refresh to try again.</p>
                    </div>
                :
                <div>
                    {aboutAuthor.imageUrl &&
                        <div className="book-page-author-details">
                            <img className="book-page-author-picture" src={aboutAuthor.imageUrl} alt="Author's image" />
                            <p className="book-page-author-name">{book && book.authors[0]}</p>
                        </div>
                    }
                    {
                        aboutAuthor.description.length < fullAuthorDescriptionRef.length ?
                        <>
                            <p className= "description-faded" >
                                {aboutAuthor.description}
                            </p>

                            <button className="show-more-btn" onClick={() => handleShowMore()}>Show more</button>
                            <svg className="arrow-down-icon" height='10' width='10' xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" >
                                <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 13.5 12 21m0 0-7.5-7.5M12 21V3" />
                            </svg>
                        </>
                        :
                        <>
                            <p className= "description" >
                                {aboutAuthor.description}
                            </p>
                            {
                                showMoreButtonClicked &&
                                <>
                                    <button className="show-more-btn" onClick={() => handleShowLess()}>Show less</button>
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
    )
}