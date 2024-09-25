import React from "react"
import { useAuthRedirect } from "../../utils/useCheckForToken";

export const StatsPage: React.FC = () => {
    useAuthRedirect();

    return (
        <div>
            Stats Page
        </div>
    )
}