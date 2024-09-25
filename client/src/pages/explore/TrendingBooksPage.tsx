import React from "react"
import { useAuthRedirect } from "../../custom-hooks/useAuthRedirect";

export const TrendingBooksPage: React.FC = () => {
    useAuthRedirect();

    return (
        <div>
            Trending Books Page
        </div>
    )
}