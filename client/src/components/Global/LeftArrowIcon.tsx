import React from "react";

interface LeftArrowProps {
    leftArrowClicked: () => void
}

export const LeftArrowIcon: React.FC<LeftArrowProps> = ({ leftArrowClicked }) => {
    return (
        <svg className="similar-books-left-arrow-pagination" fill="#000000" height="40" width="20" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 600" onClick={leftArrowClicked}>
            <path d="M49.394,154.389l150-149.996c5.857-5.858,15.355-5.858,21.213,0.001c5.857,5.858,5.857,15.355-0.001,21.213L81.213,165.002l139.393,139.389c5.857,5.858,5.857,15.355-0.001,21.213c-2.929,2.93-6.768,4.394-10.607,4.394s-7.678-1.464-10.607-4.394l-150-150.004c-2.814-2.813-4.394-6.628-4.394-10.606C45,161.018,46.58,157.202,49.394,154.389z"/>
        </svg>
    );
}