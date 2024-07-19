import React, { useEffect, useMemo, useState } from "react";
import '../../styles/search.css';
import { Genre } from "../../components/Genre";
import { fetchDefaultBooks } from "../../services/defaultBooks";
import { book } from "../../interfaces/BookInterface";
import { v4 as uuidv4 } from 'uuid';
import { getBook } from "../../services/bookSearch";

export const SearchPage: React.FC = () => {

    const genres: Array<string> = ['Romance', 'Fiction', 'Thriller', 'Action', 'Mystery', 'History', 'Scifi']

    // k = genre name, v = fetched books in the genre
    const map: Map<string, Array<book>> = new Map();

    
    // states
    const [books, setBooks] = useState(map);
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState('');

    useEffect(() => {
        const getBooks = async () => {
            try {
                const newBooks: Map<string, Array<book>> = await fetchDefaultBooks();
                setBooks(newBooks);
                setLoading(false);
            }
            catch (error) {
                console.error(error);
                setLoading(true);
            }
        }
        getBooks();
    }, []);


    const content: Array<JSX.Element> = useMemo(() => {
        return genres.map(genre => 
            <Genre key={uuidv4()} name={genre} books={books.get(genre) || []} />
        )
    }, [books])

    // functions
    const handleInput = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSearch(event.target.value);
    }

    const handleSearchClick = () => {
        // user typed something
        if (search) {
            getBook(search);
        }
    }

    return (
        <div className="search-page-container">
            <div className="search-bar">
                <svg className="search-bar-icon" width='10' height='10' viewBox="0 0 50 50" fill="black" xmlns="http://www.w3.org/2000/svg" onClick={handleSearchClick}>
                    <path d="M 21 3 C 11.621094 3 4 10.621094 4 20 C 4 29.378906 11.621094 37 21 37 C 24.710938 37 28.140625 35.804688 30.9375 33.78125 L 44.09375 46.90625 L 46.90625 44.09375 L 33.90625 31.0625 C 36.460938 28.085938 38 24.222656 38 20 C 38 10.621094 30.378906 3 21 3 Z M 21 5 C 29.296875 5 36 11.703125 36 20 C 36 28.296875 29.296875 35 21 35 C 12.703125 35 6 28.296875 6 20 C 6 11.703125 12.703125 5 21 5 Z" />
                </svg>
                <input className="search-input" type="text" placeholder="title, author, ISBN" onChange={handleInput} />
            </div>
            {loading ? 
                <p className="loading">Loading.....</p> : 
                <div className="search-default-content">
                    {content}
                </div> 
            }
        </div>
    );
}