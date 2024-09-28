import React from "react"
import { NavButton } from "./NavButton"
import '../../styles/nav.css'
import { MenuHeader } from "./MenuHeader"

export const Navbar: React.FC = () => {
    return (
        <nav className="navigation">
            <ul>
                <MenuHeader title="Explore" />
                <div className="nav-option-container">
                    <svg className="nav-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#E9C87B">
                        <path d="M15.5 14h-.79l-.28-.27a6.518 6.518 0 001.48-5.34C14.82 5.03 12.06 3 9 3S3.18 5.03 2.09 8.39a6.518 6.518 0 005.34 7.39c1.8 0 3.55-.68 4.91-1.91l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6.5 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
                    </svg>
                    <NavButton id="search-nav" name="Search" link='/app' />
                </div>
                {/* <div className="nav-option-container">
                    <svg className="nav-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#E9C87B">
                        <path d="M16 6h2.59l-7.3 7.29-3.29-3.29-6.29 6.3 1.41 1.41 4.88-4.88 3.29 3.29 8.71-8.71v2.59h2v-6h-6v2z"/>
                    </svg>
                    <NavButton id='trending-books-nav' name='Trending Books' link='/app/trending-books' />
                </div>

                <div className="nav-option-container">
                    <svg className="nav-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#E9C87B">
                        <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5s-3 1.34-3 3 1.34 3 3 3zm-8 0c1.66 0 3-1.34 3-3S9.66 5 8 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5C17 14.17 12.33 13 10 13zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
                    </svg>
                    <NavButton id='popular-authors-nav' name='Popular Authors' link='/app/popular-authors'/>
                </div> */}


                {/* <NavButton id='add-book-nav' name='Add Book' link='/app/add-book' /> */}

                <MenuHeader title='Your library' />
                <div className="nav-option-container">
                    <svg className="nav-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#E9C87B">
                        <path d="M4 2c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h8l2-2h6c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2H4zm0 2h16v16h-4l-2 2H4V4z"/>
                        <path d="M4 4v16h16V4H4zm2 2h12v2H6V6zm0 4h12v2H6v-2zm0 4h12v2H6v-2z"/>
                    </svg>
                    <NavButton id='reading-nav' name='Reading' link='/app/currently-reading' />
                </div>

                <div className="nav-option-container">
                    <svg id="star-nav-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#E9C87B">
                        <path d="M12 .587l3.668 7.429 8.232 1.193-5.95 5.198 1.407 8.19L12 18.896l-7.357 3.87 1.407-8.19-5.95-5.198 8.232-1.193z"/>
                    </svg>
                    <NavButton id='to-read-nav' name='Want to read' link='/app/to-read' />
                </div>

                <div className="nav-option-container">
                    <svg className="nav-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#E9C87B">
                        <path d="M10 15.172l-3.586-3.586L5 13l5 5 9-9-1.414-1.414z"/>
                    </svg>
                    <NavButton id='read-nav' name='Read' link='/app/read' />
                </div>

                {/* <MenuHeader title='Your activity' />
                <NavButton id='goals-nav' name='Goals' link='/app/goals' />
                <NavButton id='stats-nav' name='Stats' link='/app/stats' /> */}

                <MenuHeader title="Account" />
                <div className="nav-option-container">
                    <svg className="nav-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#E9C87B" stroke="#E9C87B" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                        <polyline points="16 17 21 12 16 7" />
                        <line x1="21" y1="12" x2="9" y2="12" />
                    </svg>
                    <NavButton id="logout-nav" name="Logout" link="/user/login" />
                </div>
                
            </ul>
        </nav>
    )
}