import React from "react"
import { NavButton } from "./NavButton"
import '../../styles/nav.css'
import { MenuHeader } from "./MenuHeader"

export const Navbar: React.FC = () => {
    return (
        <nav className="navigation">
            <ul>
                <MenuHeader title="Explore" />
                <NavButton id="search-nav" name="Search" link='/app' xmlns="http://www.w3.org/2000/svg" color='E9C87B' path="M 21 3 C 11.621094 3 4 10.621094 4 20 C 4 29.378906 11.621094 37 21 37 C 24.710938 37 28.140625 35.804688 30.9375 33.78125 L 44.09375 46.90625 L 46.90625 44.09375 L 33.90625 31.0625 C 36.460938 28.085938 38 24.222656 38 20 C 38 10.621094 30.378906 3 21 3 Z M 21 5 C 29.296875 5 36 11.703125 36 20 C 36 28.296875 29.296875 35 21 35 C 12.703125 35 6 28.296875 6 20 C 6 11.703125 12.703125 5 21 5 Z" />
                <NavButton id='trending-books-nav' name='Trending Books' link='/app/trending-books' />
                <NavButton id='popular-authors-nav' name='Popular Authors' link='/app/popular-authors'/>
                <NavButton id='add-book-nav' name='Add Book' link='/app/add-book' />

                <MenuHeader title='Your library' />
                <NavButton id='reading-nav' name='Reading' link='/app/currently-reading' />
                <NavButton id='to-read-nav' name='Want to read' link='/app/to-read' />
                <NavButton id='read-nav' name='Read' link='/app/read' />

                <MenuHeader title='Your activity' />
                <NavButton id='goals-nav' name='Goals' link='/app/goals' />
                <NavButton id='stats-nav' name='Stats' link='/app/stats' />

                <MenuHeader title="Account" />
                <NavButton id="logout-nav" name="Logout" link="/user/login" />
                
            </ul>
        </nav>
    )
}