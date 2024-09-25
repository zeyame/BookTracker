import React from "react"
import { useAuthRedirect } from "../../utils/useCheckForToken";

export const GoalsPage: React.FC = () => {
    useAuthRedirect();

    return (
        <div>
            Goals Page
        </div>
    )
}