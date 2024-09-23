import React from "react"
import { BookListByStatus } from "../../components/Global/BookListByStatus"
import { BookPageByStatus } from "../../components/Global/BookPageByStatus"
import "../../styles/reading-page.css";
import { ReadingStatus } from "../../interfaces/ReadingStatus";

export const ReadPage: React.FC = () => {
    return (
        <div className="reading-page-container">
            <BookListByStatus status={ReadingStatus.Read} />
            <BookPageByStatus />
        </div>
    )
}