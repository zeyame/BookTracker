import React from "react"
import { useAuthRedirect } from "../../custom-hooks/useAuthRedirect";

export const GoalsPage: React.FC = () => {
    useAuthRedirect();

    return (
        <div>
            Goals Page
        </div>
    )
}