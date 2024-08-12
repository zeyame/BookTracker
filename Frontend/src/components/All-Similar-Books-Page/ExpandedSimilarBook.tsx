import React from "react";
import { book } from "../../interfaces/BookInterface";

interface ExpandedSimilarBookProps {
    similarBook: book
}

export const ExpandedSimilarBook: React.FC<ExpandedSimilarBookProps> = ({ similarBook }) => {
    return (
        <div className="expanded-similar-book">
            <img className="expanded-similar-book-cover" src={similarBook.image_url} alt="Book cover" />
            <div className="expanded-similar-book-details">
                <h3 className="expanded-similar-book-title">
                    {similarBook.title}
                </h3>
                <div className="expanded-similar-book-authors">
                    by {similarBook.authors.map(author => 
                        <p key={author} className="expanded-similar-book-author">
                            {author}
                        </p>
                    )}
                </div>
                <p className="expanded-similar-book-description">
                    {similarBook.description}
                </p>
                <button className="expanded-similar-book-reading-status-btn">
                    Want to Read
                </button>
            </div>
        </div>
    )
}