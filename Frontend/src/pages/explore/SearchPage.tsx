import React, { useEffect, useMemo, useRef, useState, useCallback } from "react";
import '../../styles/search.css';
import { Genre } from "../../components/Genre";
import { fetchDefaultBooks, getCachedBooks, initializeCaching, updateCache } from "../../services/defaultBooks";
import { book } from "../../interfaces/BookInterface";
import { getBooks } from "../../services/bookSearch";
import { SearchRow } from "../../components/SearchRow";
import { LoadingIcon } from "../../components/LoadingIcon";

export const SearchPage: React.FC = () => {
    const genres: Array<string> = ['romance', 'fiction', 'thriller', 'action', 'mystery', 'history', 'scifi', 'horror', 'fantasy'];

    // states
    const [books, setBooks] = useState<Map<string, Array<book>>>(new Map());
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState('');
    const [searchResults, setSearchResults] = useState<Array<book> | null>(null);
    const [isFetching, setIsFetching] = useState<boolean>(false);
    const [booksError, setBooksError] = useState<string>('');
    const [initialBooksFetched, setInitialBooksFetched] = useState<boolean>(false);
    const [paginationLoading, setPaginationLoading] = useState<string>('');
    const [cacheInitialized, setCacheInitialized] = useState(false);

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
                setBooksError('Search');
                setSearchResults(null);
            }
        } finally {
            if (!signal?.aborted) {
                setIsFetching(false);
            }
        }
    }, []);

    // use effects
    useEffect(() => {
        if (sessionStorage.getItem('defaultBooksCache')) {
            const defaultBooksCache: string | null = sessionStorage.getItem('defaultBooksCache');
            if (defaultBooksCache) {
                const parsedCache = JSON.parse(defaultBooksCache);
                const defaultBooks = new Map<string, Array<book>>(Object.entries(parsedCache));
                setBooks(defaultBooks);
                setLoading(false);
            }
        } else {
            const getDefaultBooks = async () => {
                try {
                    const newBooks: Map<string, Array<book>> = await fetchDefaultBooks();
                    setBooks(newBooks);
                    setInitialBooksFetched(true);
                } catch (error: any) {
                    console.error(error);
                    setBooksError('Default books');
                } finally {
                    setLoading(false);
                }
            }
            getDefaultBooks();
        }
    }, []);

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
        if (books.size > 0) {
            const defaultBooksObject = Object.fromEntries(books);
            sessionStorage.setItem('defaultBooksCache', JSON.stringify(defaultBooksObject));
        }
    }, [books]);

    useEffect(() => {
        if (initialBooksFetched && !cacheInitialized) {
            initializeCaching().then(() => setCacheInitialized(true)).catch(console.error);
        }
    }, [initialBooksFetched, cacheInitialized]);

    // functions
    const handlePagination = async (genreName: string) => {
        setPaginationLoading(genreName);
        try {
            if (!cacheInitialized) {
                await initializeCaching();
                setCacheInitialized(true);
            }
            const newBooks: Array<book> = await getCachedBooks(genreName);

            const updatedBooksMap: Map<string, Array<book>> = new Map(); 
            books.forEach((books, genre) => 
                genre === genreName ? updatedBooksMap.set(genre, [...books, ...newBooks]) : updatedBooksMap.set(genre, books));
            setBooks(updatedBooksMap);
            
            await updateCache(genreName);
        } catch (error) {
            console.error(`Failed to fetch paginated books for ${genreName} genre.`, error);
        } finally {
            setPaginationLoading('');
        }
    };

    const content: Array<JSX.Element> = useMemo(() => {
        return genres.map(genre => 
            <Genre key={genre} name={genre} books={books.get(genre) || []} svgClick={handlePagination} loading={genre === paginationLoading}/>
        )
    }, [books, genres, paginationLoading]);

    const handleSearchInput = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSearch(event.target.value);
        booksError === 'Search' && setBooksError('');
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
                    isFetching ? <div className="searching-message">Searching...</div> :
                    searchResults ? 
                        <div className="search-results-container">
                            {searchResults.map(book => 
                                <SearchRow key={book.id} book={book} />
                            )}
                        </div>
                    : ''  
                }
                {booksError === 'Search' && <div className="no-results-row">No search results found.</div>}
            </div>
            { loading ? 
                <div className="default-books-loading">
                    <p>Loading</p>
                    <LoadingIcon />
                </div>
                :
                booksError === 'Default books' ? <div className="default-books-error">Failed to fetch default books. Please refresh the page.</div> :
                <div className="search-default-content">
                    {content}
                </div> 
            }
        </div>
    );
}