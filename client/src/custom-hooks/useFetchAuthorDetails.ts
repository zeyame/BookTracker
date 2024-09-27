import { useEffect, useRef, useState } from "react"
import { Author } from "../interfaces/AuthorInterface";
import { book } from "../interfaces/BookInterface";
import { sliceDescription } from "../utils/sliceDescription";
import { fetchAuthorDetails } from "../services/authorSearch";

export const useFetchAuthorDetails = (book: book | null) => {

    // states
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<boolean>(false);
    const [aboutAuthor, setAboutAuthor] = useState<Author>({
        description: '',
        imageUrl: ''
    });
    const [showMoreButtonClicked, setShowMoreButtonClicked] = useState<boolean>(false);

    // refs
    const fullAuthorDescriptionRef = useRef<string>('');


    // fetches description about the author of the book
    useEffect(() => {
        fetchDescription();
    
        return () => {
            setAboutAuthor(prevState => ({
                ...prevState,
                'description': '',
                'imageUrl': ''
            }));
        };
    }, [book]);

    const fetchDescription = async () => {
        try {
            if (book) {
                const storedAuthorDetails = sessionStorage.getItem(`author-${book.authors[0]}`);
                if (storedAuthorDetails) {
                    getStoredAuthorDetails(storedAuthorDetails);
                } 
                else {
                    setLoading(true);
                    const authorDetails: Author | null = await fetchAuthorDetails(book.authors[0]);

                    // if fetching author details did not return empty, we get the description
                    if (authorDetails) {
                        const authorDescription = authorDetails.description;
                        const imageUrl = authorDetails.imageUrl;
                        
                        fullAuthorDescriptionRef.current = authorDescription;

                        setAboutAuthor(prevState => ({
                            ...prevState,
                            description: sliceDescription(authorDescription),
                            imageUrl: imageUrl
                        }));
                        sessionStorage.setItem(`author-${book.authors[0]}`, JSON.stringify({
                            'description': authorDescription,
                            'imageUrl': imageUrl
                        }));
                    }
                }
            }
        } 
        catch (error) {
            setError(true);
        } 
        finally {
            setLoading(false);
        }
    };


    const getStoredAuthorDetails = (storedAuthorDetails: string) => {
        const parsedDetails: Author = JSON.parse(storedAuthorDetails);
        const storedDescription = parsedDetails.description;

        const storedImage = parsedDetails.imageUrl;
        fullAuthorDescriptionRef.current = storedDescription;

        setAboutAuthor(prevState => ({
            ...prevState,
            'description': sliceDescription(storedDescription),
            'imageUrl': storedImage
        }));
    }

    const handleShowMore = () => {
        setAboutAuthor(prevState => ({
            ...prevState,
            'description': fullAuthorDescriptionRef.current
        }));
        setShowMoreButtonClicked(true);
    }

    const handleShowLess = () => {
        const currentDescription = aboutAuthor.description;
        const slicedDescription = sliceDescription(currentDescription);
        setAboutAuthor(prevState => ({
            ...prevState,
            'description': slicedDescription
        }));
        setShowMoreButtonClicked(false);
    }

    return {
        loading, 
        error,
        aboutAuthor,
        showMoreButtonClicked,
        fullAuthorDescriptionRef,
        handleShowMore, 
        handleShowLess
    }



}