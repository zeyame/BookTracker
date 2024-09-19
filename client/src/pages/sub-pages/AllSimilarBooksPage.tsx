import React, { useEffect, useMemo } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { book } from "../../interfaces/BookInterface";
import '../../styles/all-similar-books-page.css';
import { SearchBar } from "../../components/Global/SearchBar";
import { ExpandedSimilarBook } from "../../components/All-Similar-Books-Page/ExpandedSimilarBook";

export const AllSimilarBooksPage: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const originalBook: book | null = location.state?.originalBook ?? null;
    const similarBooks: Array<book> | null = location.state?.similarBooks ?? null;
    
    if (!originalBook || !similarBooks || similarBooks.length === 0) {
        navigate('/');
        return null;
    }

    return (
        <div className="all-similar-books-page-container">
            <SearchBar />
            <div className="all-similar-books-page-main-content">
                <h2>Readers who enjoyed</h2>
                <ExpandedSimilarBook similarBook={originalBook} />
                <h2>also enjoyed</h2>
                {
                    similarBooks.map(book => 
                        <ExpandedSimilarBook key={book.id} similarBook={book} />
                    )
                }
            </div>
        </div>
    );
}