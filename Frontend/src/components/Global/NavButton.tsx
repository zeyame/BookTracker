import React from "react"
import { Link } from "react-router-dom"

interface NavButtonProps {
    id: string
    name: string
    link: string
    xmlns?: string
    color?: string
    path?: string
}

export const NavButton: React.FC<NavButtonProps> = ({id, name, link, xmlns, color, path}) => {
    return (
        <li id={id} className="nav-option">
            <Link to={link} className="nav-link">
                <svg className="nav-icon" xmlns={xmlns} width='12' height='12' viewBox='0 0 50 50' fill={color ? `#${color}` : undefined} >
                    <path d={path} />
                </svg>
                <p>
                    {name}
                </p>
            </Link>
        </li>
    )
}