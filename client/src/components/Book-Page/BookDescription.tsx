import React from "react";

interface BookDescriptionProps {
    bookDescription: string
    fullBookDescription: string
    bookShowMoreClicked: boolean
    handleShowMore: () => void
    handleShowLess: () => void
}

export const BookDescription: React.FC<BookDescriptionProps> = ({ bookDescription, fullBookDescription, bookShowMoreClicked, handleShowMore, handleShowLess }) => {
    return (
        <div className="book-page-description">
            { bookDescription.length < fullBookDescription.length ?
                <>
                    <p className= "description-faded" >
                        {bookDescription}
                    </p>

                    <button className="show-more-btn" onClick={handleShowMore}>Show more</button>
                    <svg className="arrow-down-icon" height='10' width='10' xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" onClick={handleShowMore} >
                        <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 13.5 12 21m0 0-7.5-7.5M12 21V3" />
                    </svg>
                </>
                :
                <>
                    <p className= "description" >
                        {bookDescription}
                    </p>
                    {
                        bookShowMoreClicked &&
                        <>
                            <button className="show-more-btn" onClick={handleShowLess}>Show less</button>
                            <svg className="arrow-down-icon" height='10' width='10' xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" onClick={handleShowLess} >
                                <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 13.5 12 21m0 0-7.5-7.5M12 21V3" />
                            </svg>
                        </>
                    }
                </>
            }
        </div>
    )
}