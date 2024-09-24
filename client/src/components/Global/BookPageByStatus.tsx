import React, { useEffect, useRef, useState } from "react";
import { BookWithStatus } from "../../interfaces/BookWithStatus";
import "../../styles/book-page-by-status.css";
import { Link } from "react-router-dom";
import { sliceDescriptionBySentences } from "../../utils/sliceDescription";

interface BookPageByStatusProps {
    book?: BookWithStatus;
}

export const BookPageByStatus: React.FC<BookPageByStatusProps> = ({ book }) => {
    const [numberOfSentences, setNumberOfSentences] = useState<number>(0);

    const getNumberOfSentences = (description: string): number => {
        const sentences: Array<string> = description.match(/[^.!?]+[.!?]+/g) || [];
        return sentences.length;
    };

    useEffect(() => {
        if (book) {
            const sentenceCount = getNumberOfSentences(book.bookData.description);
            setNumberOfSentences(sentenceCount);
        }
    }, [book]);

    let numberOfDivisions: number = numberOfSentences <= 3 ? 2 : 3;

    return (
        <div className="book-page-by-status-container">
            {book ? (
                <div className="book-page-by-status-main">
                    <div className="book-page-by-status-header">
                        <img src={book.bookData.imageUrl} className="book-page-by-status-cover" alt="Book cover" />
                        <div className="book-page-by-status-title-author-container">
                            <h2 className="book-page-by-status-title">
                                {book.bookData.title}
                            </h2>
                            <Link to={`/author/${book.bookData.authors[0]}`}>
                                <p className="book-page-by-status-author">
                                    {book.bookData.authors[0]}
                                </p>
                            </Link>
                            <button className="book-page-by-status-reading-status-button">
                                {book.status}
                            </button>
                        </div>
                    </div>
                    {book.bookData.description.length > 0 && (
                        <div className="book-page-by-status-overview">
                            <h3 style={{ margin: "0px" }}>Overview</h3>
                            <div className="book-page-by-status-book-description">
                                <p>
                                    {sliceDescriptionBySentences(book.bookData.description, 0, Math.floor(numberOfSentences / numberOfDivisions))}
                                </p>
                                <p>
                                    {sliceDescriptionBySentences(book.bookData.description, Math.floor(numberOfSentences / numberOfDivisions), Math.floor((numberOfSentences * 2) / numberOfDivisions))}
                                </p>
                                <p>
                                    {sliceDescriptionBySentences(book.bookData.description, Math.floor((2 * numberOfSentences) / numberOfDivisions), numberOfSentences - Math.floor((2 * numberOfSentences) / numberOfDivisions))}
                                </p>
                            </div>
                            <div className="book-page-by-status-author-description">
                                {book.authorDescription}
                            </div>
                        </div>
                    )}
                </div>
            ) : (
                <p>No book</p>
            )}
        </div>
    );
};
