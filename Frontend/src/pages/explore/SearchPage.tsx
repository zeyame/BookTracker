import React, { useEffect, useMemo, useRef, useState, useCallback } from "react";
import '../../styles/search.css';
import { Genre } from "../../components/Genre";
import { fetchDefaultBooks, getCachedBooks, initializeCaching, updateCache } from "../../services/defaultBooks";
import { book } from "../../interfaces/BookInterface";
import { LoadingIcon } from "../../components/LoadingIcon";
import { SearchBar } from "../../components/SearchBar";

export const SearchPage: React.FC = () => {
    const genres: Array<string> = ['romance', 'fiction', 'thriller', 'action', 'mystery', 'history', 'scifi', 'horror', 'fantasy'];

    // states
    const [books, setBooks] = useState<Map<string, Array<book>>>(new Map());
    const [loading, setLoading] = useState(true);
    const [initialBooksFetched, setInitialBooksFetched] = useState<boolean>(false);
    const [paginationLoading, setPaginationLoading] = useState<string>('');
    const [cacheInitialized, setCacheInitialized] = useState<boolean>(false);
    const [error, setError] = useState<boolean>();

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
                    setError(true);
                } finally {
                    setLoading(false);
                }
            }
            getDefaultBooks();
        }
    }, []);

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


    return (
        <div className="search-page-container">
            <div className="search-bar-container">
                <SearchBar />
            </div>
            { loading ? 
                <div className="default-books-loading">
                    <p>Loading</p>
                    <LoadingIcon />
                </div>
                :
                error ? <div className="default-books-error">Failed to fetch default books. Please refresh the page.</div> :
                <div className="search-default-content">
                    {content}
                </div> 
            }
        </div>
    );
}