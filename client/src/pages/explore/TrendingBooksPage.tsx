import React from "react"
import { useAuthRedirect } from "../../utils/useCheckForToken";

export const TrendingBooksPage: React.FC = () => {
    useAuthRedirect();

    return (
        <div>
            Trending Books Page
        </div>
    )
}