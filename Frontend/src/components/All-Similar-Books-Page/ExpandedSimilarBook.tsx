import React, { useEffect, useRef, useState } from "react";
import { book } from "../../interfaces/BookInterface";
import { sliceDescription } from "../../utils/sliceDescription";

interface ExpandedSimilarBookProps {
    similarBook: book
}

export const ExpandedSimilarBook: React.FC<ExpandedSimilarBookProps> = ({ similarBook }) => {

    // states
    const [bookDescription, setBookDescription] = useState<string>('');

    // refs
    const fullBookDescriptionRef = useRef<string>(similarBook.description);

    // effects
    useEffect(() => {
        setBookDescription(sliceDescription(similarBook.description));
    }, [similarBook]);

    return (
        <div className="expanded-similar-book">
            <img className="expanded-similar-book-cover" src={similarBook.image_url} alt="Book cover" />
            <div className="expanded-similar-book-details">
                <h3 className="expanded-similar-book-title">
                    {similarBook.title}
                </h3>
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
                            <button className="expanded-similar-book-showmore-btn">Show more</button>
                            <svg className="arrow-down-icon" height='10' width='10' xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 13.5 12 21m0 0-7.5-7.5M12 21V3" />
                            </svg>
                        </div>
                    
                    </>
                    :
                        <p className="expanded-similar-book-description">
                            {bookDescription}
                        </p>
                }
                <button className="expanded-similar-book-reading-status-btn">
                    Want to Read
                </button>
            </div>
        </div>
    );
}