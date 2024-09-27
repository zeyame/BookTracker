import { useEffect, useRef, useState } from "react";
import { book } from "../interfaces/BookInterface";
import { fetchSimilarBooks } from "../services/similarBookSearch";

export const useFetchSimilarBooks = (book: book | null ) => {

    // states
    const [similarBooks, setSimilarBooks] = useState<Array<book>>([]);
    const [similarBooksHistory, setSimilarBooksHistory] = useState<Array<Array<book>>>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<boolean>(false);

    const similarBooksCacheRef = useRef<Array<book>>([]);
    const allSimilarBooksRef = useRef<Array<book>>([]);
    
    // fetches a specified number of similar books 
    useEffect(() => {
        getSimilarBooks();

        return () => {
            setSimilarBooks([]);
            setSimilarBooksHistory([]);
        }
    }, [book]);

    const getSimilarBooks = async () => {
        if (book) {
            try {
                if (sessionStorage.getItem(`${book.title}-similar-books`)) {
                    getStoredSimilarBooks();
                }
                else {
                    setLoading(true);

                    const similarBooks: Array<book> | null = await fetchSimilarBooks(book.title, 20);
                    if (similarBooks && similarBooks.length > 0) {
                        allSimilarBooksRef.current = [...similarBooks];
                        similarBooksCacheRef.current = [...similarBooks];

                        const newSimilarBooks = similarBooksCacheRef.current.slice(0, 5);
                        
                        // updating history
                        setSimilarBooksHistory([newSimilarBooks]);

                        setSimilarBooks(newSimilarBooks);

                        // saving full cache to storage and removing the retrieved books 
                        sessionStorage.setItem(`${book.title}-similar-books`, JSON.stringify(allSimilarBooksRef.current));
                        similarBooksCacheRef.current.splice(0, 5);
                    }
                }
            }   
            catch {
                setError(true);
            }
            finally {
                setLoading(false);
            }
        }
    }

    const getStoredSimilarBooks = () => {
        if (book) {
            const storedSimilarBooks = sessionStorage.getItem(`${book.title}-similar-books`);
            if (storedSimilarBooks) {
                const similarBooks: Array<book> = JSON.parse(storedSimilarBooks);

                allSimilarBooksRef.current = [...similarBooks];
    
                // dynamic cache
                similarBooksCacheRef.current = [...similarBooks];
    
                const newSimilarBooks = similarBooksCacheRef.current.slice(-5);
    
                // updating history
                setSimilarBooksHistory([newSimilarBooks]);
                setSimilarBooks(newSimilarBooks);
                similarBooksCacheRef.current.splice(-5);
            }
        }
    }
    
    // gets the last 5 similar books from the history
    const handleLeftArrowClick = () => {
        if (similarBooksHistory.length > 0) {
            // updating history
            const newHistory = similarBooksHistory.slice(0, -1);

            if (newHistory.length > 0) {
                // sending back current similar books to the cache
                similarBooksCacheRef.current.unshift(...similarBooks);
                const lastSimilarBooks = newHistory[newHistory.length-1];
                setSimilarBooks(lastSimilarBooks);
                setSimilarBooksHistory(newHistory);
            }
        }
    }

    // gets the last 5 similar books from the cache
    const handleRightArrowClick = () => {
        if (similarBooksCacheRef.current.length > 0) {
            const newSimilarBooks = similarBooksCacheRef.current.slice(0, 5);
            setSimilarBooksHistory(prevHistory => [...prevHistory, newSimilarBooks]);
            setSimilarBooks(newSimilarBooks);
            similarBooksCacheRef.current.splice(0, 5);
        }
    }


    return {
        similarBooks,
        loading, 
        error,
        handleLeftArrowClick,
        handleRightArrowClick,
        allSimilarBooksRef
    }


}