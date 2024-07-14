import React from "react"

interface NavButtonProps {
    id: string
    name: string
    xmlns?: string
    color?: string
    path?: string
}

export const NavButton: React.FC<NavButtonProps> = ({id, name, xmlns, color, path}) => {
    return (
        <li id={id} className="nav-option">
            <a className="nav-link">
                <svg className="nav-icon" xmlns={xmlns} width='12' height='12' viewBox='0 0 50 50' fill={color ? `#${color}` : undefined}>
                    <path d={path} />
                </svg>
                <p>
                    {name}
                </p>
            </a>
        </li>
    )
}