import React from "react";
import GarbageIcon from "../Global/GarbageIcon";

export const ShelfModal: React.FC = () => {
    return (
        <>
            <div className="modal-backdrop"></div>
            <div className="shelf-modal">
                <div className="modal-title-container">
                    <h3 className="modal-title">
                        Choose a shelf for this book
                    </h3>
                    <button className="exit-modal-button">x</button>
                </div>
                <button className="modal-want-to-read-button">
                    Want to read
                </button>
                <button className="modal-currently-reading-button">
                    Currently reading
                </button>
                <button className="modal-read-button">
                    Read
                </button>
                <div className="modal-remove-from-shelf-container">
                    <GarbageIcon classname="shelf-modal-garbage-icon" />
                    <button className="modal-remove-from-shelf-button">
                        Remove from my shelf
                    </button>
                </div>
                <button className="modal-done-button">
                    Done
                </button>
            </div>
        </>
    )
}