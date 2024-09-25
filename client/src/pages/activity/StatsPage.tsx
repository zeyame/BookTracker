import React from "react"
import { useAuthRedirect } from "../../custom-hooks/useAuthRedirect";

export const StatsPage: React.FC = () => {
    useAuthRedirect();

    return (
        <div>
            Stats Page
        </div>
    )
}