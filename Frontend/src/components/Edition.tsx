import React from "react";
import { edition } from "../interfaces/EditionInterface";

interface EditionProps {
    edition: edition
}

export const Edition: React.FC<EditionProps> = ({ edition }) => {
    return (
        <div className="edition">
            <img className="edition-cover" src={edition.image_url} alt="Edition Cover" />
            <div className="edition-publisher">{edition.publisher}</div>
            <div className="edition-published-date">{edition.publishedYear}</div>
        </div>
    )
}