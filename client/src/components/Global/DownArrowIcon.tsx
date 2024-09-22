import React from 'react';

interface DownArrowProps {
    className: string
    height?: string
    width?: string
}

export const DownArrowIcon: React.FC<DownArrowProps> = ({className, height, width}) => {
  return (
    <svg
      className={className}
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      width={width ? width : "24"}
      height={height ? height : "24"}
      fill="currentColor"
    >
      <path d="M12 16.5L5.5 10 6.91 8.59 12 13.67l5.09-5.08L18.5 10z" />
    </svg>
  );
};
