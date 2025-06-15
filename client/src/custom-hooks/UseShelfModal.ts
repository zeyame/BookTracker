import { useState } from "react";
import { book } from "../interfaces/BookInterface";
import { ReadingStatus } from "../interfaces/ReadingStatus";
import { addUserBookByStatus, doesUserHaveBook, removeUserBook, updateUserBookStatus } from "../services/userBookService";
import { fetchDefaultBooks } from "../services/defaultBookSearch";

export const useShelfModal = (
    bookStatus: string,
    setBookStatus: (status: string) => void,
    setShowPopUp: (show: boolean) => void,
    book: book | null,
    token: string,
    authorDescription?: string,
    onStatusChange?: () => void
  ) => {

    const [showModal, setShowModal] = useState<boolean>(false);
    const [selectedShelf, setSelectedShelf] = useState<string>("");
    const [showRemoveFromShelfModal, setShowRemoveFromShelfModal] = useState<boolean>(false);

  
    const handleExitModal = () => {
      setShowModal(false);
    };
  
    const handleModalWantToRead = () => {
      setSelectedShelf(ReadingStatus.WantToRead);
    };
  
    const handleCurrentlyReading = () => {
      setSelectedShelf(ReadingStatus.CurrentlyReading);
    };
  
    const handleRead = () => {
      setSelectedShelf(ReadingStatus.Read);
    };
  
    const handleRemoveFromShelf = () => {
      setShowModal(false);
      setShowRemoveFromShelfModal(true);
    };
  
    const handleDone = async () => {
        if (!book || !selectedShelf || bookStatus == selectedShelf) {
          setShowModal(false);
          return;
        }

        setShowModal(false);

        try {
            const alreadyExists = await doesUserHaveBook(book.id, token);

            if (alreadyExists) {
                // Update existing entry
                await updateUserBookStatus(book.id, selectedShelf, token);
            } else {
                // Add new entry
                await addUserBookByStatus(book, selectedShelf, token, authorDescription);
            }

            setBookStatus(selectedShelf);
            setShowPopUp(true);
            setTimeout(() => setShowPopUp(false), 3000);
        } catch (error) {
            console.error("Failed to persist book status:", error);
        }
    };

  
    // REMOVE FROM SHELF MODAL FUNCTIONS

    // exiting the remove from shelf modal
    const handleExitRemoveFromShelfModal = () => {
        setShowRemoveFromShelfModal(false);
        setShowModal(true);
    }

    // removing a book from its current shelf
    const handleRemoveFromShelfButton = async (bookToRemove: book | null): Promise<void> => {
        if (!bookToRemove) return;

        try {
            await removeUserBook(bookToRemove.id, token);
            setBookStatus(""); // clear status
            setShowRemoveFromShelfModal(false);
            if (onStatusChange) onStatusChange();
        } catch (error) {
            console.error("Failed to remove book from backend:", error);
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
  