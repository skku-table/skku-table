// components/LikeButton.tsx
'use client';

import { useState } from "react";
import { IoHeartOutline, IoHeartSharp } from "react-icons/io5";

interface LikeButtonProps {
  initialLiked?: boolean;
  className?: string;
  size?: number;
  onClick?: () => void; 
}

export default function LikeButton({
  initialLiked = false,
  className = "",
  size = 24,
  onClick,
}: LikeButtonProps) {
  const [liked, setLiked] = useState(initialLiked);
  const Icon = liked ? IoHeartSharp : IoHeartOutline;

  const handleClick = () => {
    setLiked(!liked);
    if (onClick) onClick();
  };

  return (
    <button
      onClick={handleClick}
      className={`absolute top-3 right-3 p-0 bg-transparent border-none ${className}`}
      style={{ lineHeight: 0 }}
    >
      <Icon
        size={size}
        style={{
          color: liked ? "red" : "white",
        }}
      />
    </button>
  );
}

