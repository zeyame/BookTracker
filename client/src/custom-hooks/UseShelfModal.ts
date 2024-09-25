import { useEffect, useState } from "react";
import { book } from "../interfaces/BookInterface";
import { BookWithStatus } from "../interfaces/BookWithStatus";

export const useShelfModal = (bookStatus: string, setBookStatus: (status: string) => void, setShowPopUp: (showPopUp: boolean) => void) => {
    const [showModal, setShowModal] = useState<boolean>(false);
    const [selectedShelf, setSelectedShelf] = useState<string>("");
    const [showRemoveFromShelfModal, setShowRemoveFromShelfModal] = useState<boolean>(false);

  
    const handleExitModal = () => {
      setShowModal(false);
    };
  
    const handleModalWantToRead = () => {
      setSelectedShelf("Want to read");
    };
  
    const handleCurrentlyReading = () => {
      setSelectedShelf("Currently reading");
    };
  
    const handleRead = () => {
      setSelectedShelf("Read");
    };
  
    const handleRemoveFromShelf = () => {
      setShowModal(false);
      setShowRemoveFromShelfModal(true);
    };
  
    const handleDone = () => {
      setShowModal(false);

      if (selectedShelf.length > 0) {
        setBookStatus(selectedShelf);
      }

        // if the selected option was not the same as the current reading status we show a new message popup
        if (selectedShelf.length > 0 && selectedShelf !== bookStatus) {
            setShowPopUp(true);
            // Hide the popup after 3 seconds
            setTimeout(() => {
                setShowPopUp(false);
            }, 3000);
        }
    };
  
    // REMOVE FROM SHELF MODAL FUNCTIONS

    // exiting the remove from shelf modal
    const handleExitRemoveFromShelfModal = () => {
        setShowRemoveFromShelfModal(false);
        setShowModal(true);
    }

    // removing a book from its current shelf
    const handleRemoveFromShelfButton = (book: book | null): void => {
        if (!book) {
            console.warn("Attempted to remove a null book from shelf");
            return;
        }
    
        const storedBooksWithStatus: string | null = localStorage.getItem("booksWithStatus");
    
        if (!storedBooksWithStatus) {
            console.warn("No books with status found in localStorage");
            return;
        }
    
        try {
            let books: Record<string, BookWithStatus> = JSON.parse(storedBooksWithStatus);
    
            if (book.id in books) {
                // Remove the book entirely instead of setting status to empty string
                delete books[book.id];
    
                // Update localStorage
                localStorage.setItem("booksWithStatus", JSON.stringify(books));
    
                // Update local state
                setBookStatus("");
                setShowRemoveFromShelfModal(false);
            } else {
                console.warn(`Book "${book.title}" not found in stored books`);
            }
        } catch (error) {
            console.error("Error parsing or updating stored books:", error);
        }
    };


    return {
      showModal,
      setShowModal,
      selectedShelf,
      showRemoveFromShelfModal,
      handleExitModal,
      handleModalWantToRead,
      handleCurrentlyReading,
      handleRead,
      handleRemoveFromShelf,
      handleDone,
      handleExitRemoveFromShelfModal,
      handleRemoveFromShelfButton
    };
  };
  