import React from "react";
import GarbageIcon from "./GarbageIcon";
import TickIcon from "../Book-Page/TickIcon";
import { ReadingStatus } from "../../interfaces/ReadingStatus";

interface ShelfModalProps {
    handleExitModal: () => void
    handleModalWantToRead: () => void
    handleCurrentlyReading: () => void
    handleRead: () => void
    handleRemoveFromShelf: () => void
    handleDone: () => void
    selectedShelf: string
}


export const ShelfModal: React.FC<ShelfModalProps> = ({handleExitModal, handleModalWantToRead, handleCurrentlyReading, handleRead, handleRemoveFromShelf, handleDone, selectedShelf}) => {
    return (
        <>
            <div className="modal-backdrop"></div>
            <div className="shelf-modal">
                <div className="modal-title-container">
                    <h3 className="modal-title">
                        Choose a shelf for this book
                    </h3>
                    <button className="exit-modal-button" onClick={handleExitModal}>x</button>
                </div>

                <button className={`shelf-modal-button ${selectedShelf===ReadingStatus.WantToRead && 'selected'}`} onClick={handleModalWantToRead}>
                    {selectedShelf===ReadingStatus.WantToRead && <TickIcon classname="shelf-modal-tick-icon" width="10" height="10" />}
                    Want to read
                </button>

                <button className={`shelf-modal-button ${selectedShelf===ReadingStatus.CurrentlyReading && 'selected'}`} onClick={handleCurrentlyReading}>
                    {selectedShelf===ReadingStatus.CurrentlyReading && <TickIcon classname="shelf-modal-tick-icon" width="18" />}
                    Currently reading
                </button>

                <button className={`shelf-modal-button ${selectedShelf===ReadingStatus.Read && 'selected'}`} onClick={handleRead}>
                    {selectedShelf===ReadingStatus.Read && <TickIcon classname="shelf-modal-tick-icon" />}
                    Read    
                </button>

                <div className="modal-remove-from-shelf-container">
                    <GarbageIcon classname="shelf-modal-garbage-icon" />
                    <button className="modal-remove-from-shelf-button" onClick={handleRemoveFromShelf}>
                        Remove from my shelf
                    </button>
                </div>
                <button className="modal-done-button" onClick={handleDone}>
                    Done
                </button>
            </div>
        </>
    )
}