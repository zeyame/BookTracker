import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { book } from "../../interfaces/BookInterface";
import '../../styles/all-similar-books-page.css';
import { SearchBar } from "../../components/Global/SearchBar";
import { ExpandedSimilarBook } from "../../components/All-Similar-Books-Page/ExpandedSimilarBook";
import { useAuthRedirect } from "../../utils/useCheckForToken";

export const AllSimilarBooksPage: React.FC = () => {
    useAuthRedirect();

    const navigate = useNavigate();
    const location = useLocation();
    const originalBook: book | null = location.state?.originalBook;
    const similarBooks: Array<book> | null = location.state?.similarBooks;
    
    if (!originalBook || !similarBooks || similarBooks.length === 0) {
        navigate('/');
        return <div>No books.</div>;
    }

    return (
        <div className="all-similar-books-page-container">
            <SearchBar />
            <div className="all-similar-books-page-main-content">
                <h2>Readers who enjoyed</h2>
                <ExpandedSimilarBook similarBook={originalBook} />
                <h2>also enjoyed</h2>
                {similarBooks.map((book, index) => 
                    book ? (
                        <ExpandedSimilarBook 
                            key={book.id || `book-${index}`} 
                            similarBook={book} 
                        />
                    ) : null
                )}
            </div>
        </div>
    );
}