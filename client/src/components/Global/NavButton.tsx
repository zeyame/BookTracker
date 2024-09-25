import React from "react"
import { Link, useNavigate } from "react-router-dom"

interface NavButtonProps {
    id: string
    name: string
    link: string
}

export const NavButton: React.FC<NavButtonProps> = ({id, name, link}) => {

    const navigate = useNavigate();

    const handleLogout = () => {
        sessionStorage.removeItem("token");
        navigate("/user/login", { replace: true });
    };


    return (
        <li id={id} className="nav-option">
            {
                name === "Logout" ?
                <Link to={link} className="nav-link" onClick={handleLogout} replace>
                    <p>
                        {name}
                    </p>
                </Link>
                :
                <Link to={link} className="nav-link" >
                    <p>
                        {name}
                    </p>
                </Link>
            }
        </li>
    )
}