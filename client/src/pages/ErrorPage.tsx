import React from "react"
import { Link } from "react-router-dom"

export const ErrorPage: React.FC = () => {
    return (
        <div>
            ERROR 404
            <Link to='/'>Home</Link>
        </div>
    )
}