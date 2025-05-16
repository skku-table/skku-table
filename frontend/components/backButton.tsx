'use client';

import { useRouter } from 'next/navigation';
import { FaArrowLeftLong } from "react-icons/fa6";

export default function BackButton() {
  const router = useRouter();

  return (
    <button
      onClick={() => router.back()}
      className="text-lg font-bold p-2"
    >
      <FaArrowLeftLong/>
    </button>
  );
}
