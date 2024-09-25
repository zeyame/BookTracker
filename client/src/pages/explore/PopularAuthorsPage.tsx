import React from "react"
import { useAuthRedirect } from "../../custom-hooks/useAuthRedirect";

export const PopularAuthorsPage: React.FC = () => {
    useAuthRedirect();

    return (
        <div>
            Popular Authors Page
        </div>
    )
}