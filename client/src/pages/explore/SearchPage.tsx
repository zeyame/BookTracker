import React, { useEffect, useMemo, useRef, useState, useCallback } from "react";
import '../../styles/search-page.css';
import { Genre } from "../../components/Search-Page/Genre";
import { fetchDefaultBooks } from "../../services/defaultBookSearch";
import { initializeCaching, getCachedBooks, updateCache } from "../../services/caching";
import { book } from "../../interfaces/BookInterface";
import { LoadingIcon } from "../../components/Global/LoadingIcon";
import { SearchBar } from "../../components/Global/SearchBar";
import { useAuthRedirect } from "../../custom-hooks/useAuthRedirect";

export const SearchPage: React.FC = () => {
    useAuthRedirect();

    const genres: Array<string> = ['nonfiction', 'fiction', 'thriller', 'action', 'mystery', 'history', 'horror', 'fantasy'];

    // states
    const [books, setBooks] = useState<Map<string, Array<book>>>(new Map());
    const [loading, setLoading] = useState(true);
    const [initialBooksFetched, setInitialBooksFetched] = useState<boolean>(false);
    const [paginationLoading, setPaginationLoading] = useState<string>('');
    const [cacheInitialized, setCacheInitialized] = useState<boolean>(false);
    const [error, setError] = useState<boolean>();

    // use effects
    useEffect(() => {
        if (localStorage.getItem('defaultBooksCache')) {
            const defaultBooksCache: string | null = localStorage.getItem('defaultBooksCache');
            if (defaultBooksCache) {
                const parsedCache = JSON.parse(defaultBooksCache);
                const defaultBooks = new Map<string, Array<book>>(Object.entries(parsedCache));
                setBooks(defaultBooks);
                setInitialBooksFetched(true);
                setLoading(false);
            }
        } else {
            const getDefaultBooks = async () => {
                try {
                    const newBooks: Map<string, Array<book>> = await fetchDefaultBooks(9, genres);
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
            localStorage.setItem('defaultBooksCache', JSON.stringify(defaultBooksObject));
        }
    }, [initialBooksFetched]);

    // useEffect(() => {
    //     if (initialBooksFetched && !cacheInitialized) {
    //         initializeCaching(9).then(() => setCacheInitialized(true)).catch(console.error);
    //     }
    // }, [initialBooksFetched, cacheInitialized]);

    // functions
    const handlePagination = async (genreName: string) => {
        setPaginationLoading(genreName);
        try {
            if (!cacheInitialized) {
                await initializeCaching(9);
                setCacheInitialized(true);
            }
            const newBooks: Array<book> = await getCachedBooks(genreName, 9);
            
            setBooks(prevBooks => {
                const updatedBooks = new Map(prevBooks);
                const existingBooks = updatedBooks.get(genreName) || [];
                updatedBooks.set(genreName, [...existingBooks, ...newBooks]);
                return updatedBooks;
            });
            
            await updateCache(genreName, 9);
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