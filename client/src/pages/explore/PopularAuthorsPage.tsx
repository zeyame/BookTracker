import React from "react"
import { useAuthRedirect } from "../../utils/useCheckForToken";

export const PopularAuthorsPage: React.FC = () => {
    useAuthRedirect();

    return (
        <div>
            Popular Authors Page
        </div>
    )
}