import React, { useEffect, useMemo, useRef, useState, useCallback } from "react";
import '../../styles/search.css';
import { Genre } from "../../components/Genre";
import { fetchDefaultBooks } from "../../services/defaultBooks";
import { book } from "../../interfaces/BookInterface";
import { getBooks } from "../../services/bookSearch";
import { SearchRow } from "../../components/SearchRow";

export const SearchPage: React.FC = () => {

    const genres: Array<string> = ['Romance', 'Fiction', 'Thriller', 'Action', 'Mystery', 'History', 'Scifi', 'Horror', 'Fantasy'];

    // k = genre name, v = fetched books in the genre
    const map: Map<string, Array<book>> = new Map();

    
    // states
    const [books, setBooks] = useState(map);
    const [loading, setLoading] = useState(true);  
    const [search, setSearch] = useState('');       // stores the current search value in the search bar
    const [searchResults, setSearchResults] = useState<Array<book> | null>(null);           // storing the fetched books for a search
    const [isFetching, setisFetching] = useState<boolean>(false);           // we are in the process of fetching books
    const [error, setError] = useState<string>('');

    // refs
    const abortControllerRef = useRef<AbortController | null>(null);

    // callbacks
    const fetchBooks = useCallback(async (query: string, signal?: AbortSignal) => {
        setisFetching(true);
        try {
            const currentBooks = await getBooks(query, signal);
            if (!signal?.aborted) {
                setSearchResults(currentBooks);
            }
        }
        catch (error: any) {
            if (error.name === 'AbortError') {
                console.log(`The search with query ${query} was aborted`);
            }
            else {
                console.error(error);
                setError('Search');
                setSearchResults(null);
            }
        }
        finally {
            if (!signal?.aborted) {
                setisFetching(false);
            }
        }
    }, []);

    // effects
    // fetching books from backend and storing them in a map on mount
    useEffect(() => {
        const getDefaultBooks = async () => {
            try {
                const newBooks: Map<string, Array<book>> = await fetchDefaultBooks();
                setBooks(newBooks);
                setLoading(false);
            }
            catch (error: any) {
                console.error(error);
                setError('Default books');
            }
            finally {
                setLoading(false);
            }
        }
        getDefaultBooks();
    }, []);

    // Fetching the 5 books that match what the user has inputted so far
    useEffect(() => {
        if (search) {
            // cancelling any previous requests
            abortControllerRef.current?.abort();
            abortControllerRef.current = new AbortController();

            fetchBooks(search, abortControllerRef.current.signal);
        } 
        else {
            setSearchResults(null);
            setisFetching(false);
        }

        return () => {
            abortControllerRef.current?.abort();
        }

    }, [search]);


    // functions

    // Function memoizes the default content on the search page
    const content: Array<JSX.Element> = useMemo(() => {
        return genres.map(genre => 
            <Genre key={genre} name={genre} books={books.get(genre) || []} />
        )
    }, [books]);


    // Function updates search state as user is typing
    const handleSearchInput = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSearch(event.target.value);
        setError('');
    }


    return (
        <div className="search-page-container">
            <div className="search-bar-container">
                <div className="search-bar">
                    <input className="search-input" type="text" placeholder="title, author, ISBN" value={search} onChange={handleSearchInput} />
                    <svg className="search-bar-icon" width='10' height='10' viewBox="0 0 50 50" fill="black" xmlns="http://www.w3.org/2000/svg" >
                        <path d="M 21 3 C 11.621094 3 4 10.621094 4 20 C 4 29.378906 11.621094 37 21 37 C 24.710938 37 28.140625 35.804688 30.9375 33.78125 L 44.09375 46.90625 L 46.90625 44.09375 L 33.90625 31.0625 C 36.460938 28.085938 38 24.222656 38 20 C 38 10.621094 30.378906 3 21 3 Z M 21 5 C 29.296875 5 36 11.703125 36 20 C 36 28.296875 29.296875 35 21 35 C 12.703125 35 6 28.296875 6 20 C 6 11.703125 12.703125 5 21 5 Z" />
                    </svg>
                </div>
                {
                    isFetching ? <div>Searching...</div> :
                    searchResults ? searchResults.map(book => 
                        <SearchRow key={book.id} book={book} />
                    ) : ''  
                }
                {error === 'Search' && <div className="no-results-row">No search results found.</div>}
            </div>
            { loading ? <p className="loading">Loading.....</p> :
                error === 'Default books' ? <div className="default-books-error">Failed to fetch default books. Please refresh the page.</div> :
                <div className="search-default-content">
                    {content}
                </div> 
            }
        </div>
    );
}