'use client'

import { boothLikeStore } from '@/stores/boothLikeStores';
import Image from 'next/image';
import LikeBoothButton from './LikeBoothButton';
import { formatDate } from '@/libs/utils';
import { IoHeartSharp } from 'react-icons/io5';
import { useEffect } from 'react';

type Booth = {
  id: number;
  posterImageUrl: string;
  name: string;
  startDateTime: string;
  endDateTime: string;
  likeCount: number;
  location: string;
};

export function BoothCard({booth}: {booth: Booth}) {
  const { boothLikeCounts, setBoothLikeCount } = boothLikeStore()
  const count = boothLikeCounts[booth.id] ?? booth.likeCount

  useEffect(() => {
    if (boothLikeCounts[booth.id] === undefined) {
      setBoothLikeCount(booth.id, booth.likeCount)
    }
  }, [booth.id, boothLikeCounts])
  return(
      <>
        {/* 포스터 이미지 + 하트 버튼 */}
        <div className="relative w-full h-[270px]">
          <Image
            src={booth.posterImageUrl}
            alt="부스 포스터"
            fill
            className="object-cover"
          />
          <LikeBoothButton boothId={booth.id}/>
        </div>

        {/* 부스 기본 정보 */}
        <div>
          <div className="flex items-center gap-2 mt-4">
            <h2 className="text-xl font-bold">{booth.name}</h2>
            <div className="flex items-center gap-1 text-[15px] text-black/60">
              <IoHeartSharp size={18} className="text-red-500" />
              {count}
            </div>
          </div>
          <ul className="list-disc pl-5 text-sm space-y-1 mt-2">
            <li><strong>기간</strong> : {formatDate(booth.startDateTime)}-{formatDate(booth.endDateTime)}</li>
            <li><strong>위치</strong> : {booth.location}</li>
          </ul>
        </div>
      </>
  )

}