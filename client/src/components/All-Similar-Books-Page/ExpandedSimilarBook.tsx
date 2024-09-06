import React, { useEffect, useRef, useState } from "react";
import { book } from "../../interfaces/BookInterface";
import { sliceDescription } from "../../utils/sliceDescription";
import { Link } from "react-router-dom";

interface ExpandedSimilarBookProps {
    similarBook: book
}

export const ExpandedSimilarBook: React.FC<ExpandedSimilarBookProps> = ({ similarBook }) => {

    // states
    const [bookDescription, setBookDescription] = useState<string>('');
    const [showMoreButtonClicked, setShowMoreButtonClicked] = useState<boolean>(false);

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
            <Link to={`/book/${similarBook.id}`} state={ {bookData: similarBook} }>
                <img className="expanded-similar-book-cover" src={similarBook.imageUrl} alt="Book cover" />    
            </Link>
            <div className="expanded-similar-book-details">
                <Link to={`/book/${similarBook.id}`} state={ {bookData: similarBook} }>
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
                <button className="expanded-similar-book-reading-status-btn">
                    Want to Read
                </button>
            </div>
        </div>
    );
}