import React from "react";
import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuthRedirect } from "../../utils/useCheckForToken";

export const AuthorPage: React.FC = () => {
    useAuthRedirect();

    const location = useLocation();
    const navigate = useNavigate();

    const authorData: string | null = location.state?.authorData ?? null;

    if (!authorData) {
        navigate('/');
        return null;
    }

    return (
        <div>Hello {authorData}</div>
    );
}