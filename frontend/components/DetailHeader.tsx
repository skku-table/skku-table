// components/CommonDetailHeader.tsx

import Image from 'next/image';
import { IoHeart } from 'react-icons/io5';
import Header from '@/components/Headers';
import LikeButton from '@/components/LikeButton';
import { formatDate } from '@/libs/utils';

interface CommonDetailHeaderProps {
  name: string;
  startDate: string;
  endDate: string;
  location: string;
  posterImageUrl: string;
  likeCount: number;
}

export default function CommonDetailHeader({
  name,
  startDate,
  endDate,
  location,
  posterImageUrl,
  likeCount,
}: CommonDetailHeaderProps) {
  return (
    <div>
    {/* <div> */}

      {/* 뒤로가기 헤더 */}
      <Header isBackButton={true} title={name} />

      <div className="relative p-4 pt-16 space-y-6">
        {/* 포스터 이미지 + 좋아요 버튼 */}
        <div className="relative w-full h-[270px]">
          <Image
            src={posterImageUrl}
            alt="포스터"
            fill
            className="object-cover"
          />
          <LikeButton
            initialLiked={false}
            size={25}
            className="absolute top-2 right-2"
          />
        </div>

        {/* 이름 + 좋아요 수 + 정보 */}
        <div>
          <div className="flex items-center gap-2 mt-4">
            <h2 className="text-xl font-bold">{name}</h2>
            <div className="flex items-center gap-1 text-[15px] text-black/60">
              <IoHeart size={18} className="text-red-500" />
              {likeCount}
            </div>
          </div>

          <ul className="list-disc pl-5 text-sm space-y-1 mt-2">
            <li>
              <strong>기간</strong> : 
                {formatDate(startDate.split('T')[0])} - 
                {formatDate(endDate.split('T')[0])}
            </li>
            <li>
              <strong>위치</strong> : {location}
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
}
