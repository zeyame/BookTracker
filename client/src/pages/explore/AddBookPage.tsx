import React from "react"
import { useAuthRedirect } from "../../custom-hooks/useAuthRedirect";

export const AddBookPage: React.FC = () => {
    useAuthRedirect();

    return (
        <div>
            Add Book Page
        </div>
    )
}