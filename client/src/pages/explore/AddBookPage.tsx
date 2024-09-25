import React from "react"
import { useAuthRedirect } from "../../utils/useCheckForToken";

export const AddBookPage: React.FC = () => {
    useAuthRedirect();

    return (
        <div>
            Add Book Page
        </div>
    )
}