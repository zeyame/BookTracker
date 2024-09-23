import React from "react";

interface RemoveFromShelfModalProps {
    handleExitRemoveFromShelfModal: () => void
    handleCancelRemoveFromShelf: () => void
}

export const RemoveFromShelfModal: React.FC<RemoveFromShelfModalProps> = ({handleExitRemoveFromShelfModal, handleCancelRemoveFromShelf}) => {
    return (
        <>
            <div className="modal-backdrop"></div>
            <div className="remove-from-shelf-modal">
                <div className="remove-from-shelf-modal-title-container">
                    <h3 className="remove-from-shelf-modal-title">
                        Are you sure you want to remove this book from your shelves?
                    </h3>
                    <button className="exit-modal-button" onClick={handleExitRemoveFromShelfModal}>x</button>
                </div>
                <p className="removing-from-shelf-warning-message">
                    Removing this book will clear associated ratings, reviews, and reading activity.
                </p>
                <div className="remove-from-shelf-modal-button-container">
                    <button className="cancel-remove-from-shelf" onClick={handleCancelRemoveFromShelf}>
                        Cancel
                    </button>
                    <button className="remove-from-shelf-button">
                        Remove
                    </button>
                </div>
            </div>
        </>
    );
}