import React from "react";
import { useEffect, useState, useRef } from "react";
import { fetchAuthorDetails } from "../../services/authorSearch";
import { Author } from "../../interfaces/AuthorInterface";
import { LoadingIcon } from "../Global/LoadingIcon";

interface AboutAuthorProps {
    authorName: string
    sliceDescription: (description: string) => string
}

export const AboutAuthor: React.FC<AboutAuthorProps> = ({ authorName, sliceDescription }) => {
    // states
    const [aboutAuthor, setAboutAuthor] = useState<Author>({
        'description': '',
        'imageUrl': ''
    });
    const [showMoreButtonClicked, setShowMoreButtonClicked] = useState<boolean>(false);
    const [error, setError] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(false);

    // refs
    const fullAuthorDescription = useRef<string>('');

    // fetches description about the author of the book
    useEffect(() => {
        const fetchDescription = async () => {
            try {
                const storedAuthorDetails = sessionStorage.getItem(`author-${authorName}`);
                if (storedAuthorDetails) {
                    const parsedDetails: Author = JSON.parse(storedAuthorDetails);
                    const storedDescription = parsedDetails.description;
                    const storedImage = parsedDetails.imageUrl;
                    fullAuthorDescription.current = storedDescription;
                    setAboutAuthor(prevState => ({
                        ...prevState,
                        'description': sliceDescription(storedDescription),
                        'imageUrl': storedImage
                    }));
                } 
                else {
                    setLoading(true);
                    const authorDetails: Author | null = await fetchAuthorDetails(authorName);

                    // if fetching author details did not return empty, we get the description
                    if (authorDetails) {
                        const authorDescription = authorDetails.description;
                        const imageUrl = authorDetails.imageUrl;
                        fullAuthorDescription.current = authorDescription;

                        setAboutAuthor(prevState => ({
                            ...prevState,
                            description: sliceDescription(authorDescription),
                            imageUrl: imageUrl
                        }));
                        sessionStorage.setItem(`author-${authorName}`, JSON.stringify({
                            'description': authorDescription,
                            'imageUrl': imageUrl
                        }));
                    }
                }
            
            } catch (error) {
                setError(true);
            } finally {
                setLoading(false);
            }
        };

        fetchDescription();

        return () => {
            setAboutAuthor(prevState => ({
                ...prevState,
                'description': '',
                'imageUrl': ''
            }));
        };
    }, []);


    // functions
    const handleShowMore = () => {
        setAboutAuthor(prevState => ({
            ...prevState,
            'description': fullAuthorDescription.current
        }));
        setShowMoreButtonClicked(true);
    }

    const handleShowLess = () => {
        const currentDescription = aboutAuthor.description;
        const slicedDescription = sliceDescription(currentDescription);
        setAboutAuthor(prevState => ({
            ...prevState,
            'description': slicedDescription
        }));
        setShowMoreButtonClicked(false);
    }
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
                            <p className="book-page-author-name">{authorName}</p>
                        </div>
                    }
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
    );
}