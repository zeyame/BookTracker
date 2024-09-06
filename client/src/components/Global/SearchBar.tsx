import React, { useCallback, useEffect, useRef, useState } from "react";
import { book } from "../../interfaces/BookInterface";
import { getBooks } from "../../services/userBookSearch";
import { SearchRow } from "./SearchRow";
import { useNavigate } from "react-router-dom";

export const SearchBar: React.FC = () => {
    // states
    const [search, setSearch] = useState('');
    const [searchResults, setSearchResults] = useState<Array<book> | null>(null);
    const [error, setError] = useState<boolean>(false);
    const [isFetching, setIsFetching] = useState<boolean>(false);
    const [isNavigating, setIsNavigating] = useState<boolean>(false);
    const navigate = useNavigate();

    // refs
    const abortControllerRef = useRef<AbortController | null>(null);

    // callbacks
    const fetchBooks = useCallback(async (query: string, signal?: AbortSignal) => {
        setIsFetching(true);
        try {
            const currentBooks: Array<book> = await getBooks(query, signal);
            if (!signal?.aborted) {
                setSearchResults(currentBooks);
            }
        } catch (error: any) {
            if (error.name === 'AbortError') {
                console.log(`The search with query ${query} was aborted`);
            } else {
                setError(true);
                setSearchResults(null);
            }
        } finally {
            if (!signal?.aborted) {
                setIsFetching(false);
            }
        }
    }, []);
    
    // effects
    useEffect(() => {
        if (search) {
            abortControllerRef.current?.abort();
            abortControllerRef.current = new AbortController();
            fetchBooks(search, abortControllerRef.current.signal);
        } else {
            setSearchResults(null);
            setIsFetching(false);
        }
        return () => {
            abortControllerRef.current?.abort();
        }
    }, [search, fetchBooks]);

    useEffect(() => {
        if (isNavigating) {
            setSearchResults(null);
            setIsNavigating(false);
        }
    }, [isNavigating]);

    // functions
    const handleSearchInput = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSearch(event.target.value);
        error && setError(false);
    }

    const customNavigate = (path: string, options: any) => {
        setIsNavigating(true);
        navigate(path, options);
    }

    return (
        <>
            <div className="search-bar">
                <input className="search-input" type="text" placeholder="title, author, ISBN" value={search} onChange={handleSearchInput} />
                <svg className="search-bar-icon" width='10' height='10' viewBox="0 0 50 50" fill="black" xmlns="http://www.w3.org/2000/svg" >
                    <path d="M 21 3 C 11.621094 3 4 10.621094 4 20 C 4 29.378906 11.621094 37 21 37 C 24.710938 37 28.140625 35.804688 30.9375 33.78125 L 44.09375 46.90625 L 46.90625 44.09375 L 33.90625 31.0625 C 36.460938 28.085938 38 24.222656 38 20 C 38 10.621094 30.378906 3 21 3 Z M 21 5 C 29.296875 5 36 11.703125 36 20 C 36 28.296875 29.296875 35 21 35 C 12.703125 35 6 28.296875 6 20 C 6 11.703125 12.703125 5 21 5 Z" />
                </svg>
            </div>
            {
                isFetching ? <div className="searching-message">Searching...</div> :
                searchResults ? 
                    <div className="search-results-container">
                        {searchResults.map(book => 
                            <SearchRow key={book.id} book={book} customNavigate={customNavigate} />
                        )}
                    </div>
                : ''  
            }
            {error && <div className="no-results-row">No search results found.</div>}
        </>
    );
}