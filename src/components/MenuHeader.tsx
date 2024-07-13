import React from "react";

interface MenuHeaderProps {
    title: string
}

export const MenuHeader: React.FC<MenuHeaderProps> = ({title}) => {
    return (
        <div className="sbmenu-header">
            {title}
        </div>
    );
}