import React from 'react';

interface TickIconProps {
    classname: string
    width? : string
    height? : string
}

const TickIcon: React.FC<TickIconProps> = ({classname}) => {
    return (
        <svg
            className={classname}
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            width="24"
            height="24"
            fill="currentColor"
        >
            <path d="M9 19.4l-6.7-6.7c-.4-.4-.4-1 0-1.4s1-.4 1.4 0L9 16.6l10.3-10.3c.4-.4 1-.4 1.4 0s.4 1 0 1.4l-11 11c-.4.4-1 .4-1.4 0z" />
        </svg>
    );
};

export default TickIcon;
