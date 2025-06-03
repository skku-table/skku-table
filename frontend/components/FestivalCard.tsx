'use client'

import { useLikeStore } from '@/stores/useLikeStore'
import Image from 'next/image';
import Link from 'next/link';
import LikeFestivalButton from './LikeFestivalButton';
import { formatDate } from '@/libs/utils';
import { IoHeartSharp } from 'react-icons/io5';
import { useEffect } from 'react';

type Festival = {
  id: number;
  posterImageUrl: string;
  name: string;
  startDate: string;
  endDate: string;
  likeCount: number;
};

export function FestivalCard({ festival }: { festival: Festival }) {
  const { festivalLikeCounts, setFestivalLikeCount } = useLikeStore()
  const count = festivalLikeCounts[festival.id] ?? festival.likeCount
    // 초기값 zustand에 등록
  useEffect(() => {
    if (festivalLikeCounts[festival.id] === undefined) {
      setFestivalLikeCount(festival.id, festival.likeCount)
    }
  }, [festival.id, festivalLikeCounts])
  return(
    <div>
        <div className="relative w-[290px] h-[290px] mx-auto">
          <Link href={`/festival/${festival.id}`}>
            <Image
              src={festival.posterImageUrl}
              alt="festival poster"
              fill
              className="rounded-xl object-cover cursor-pointer"
            />
          </Link>
          <LikeFestivalButton festivalId={festival.id}/>
        </div>

      <div className="mt-2">
        <p className="text-lg font-bold">{festival.name}</p>
        <p className="text-lg">
          {formatDate(festival.startDate)} ~ {formatDate(festival.endDate)}
        </p>
        {/* <p className="flex items-center gap-1" style={{ fontSize: "15px", color: "rgba(0, 0, 0, 0.6)" }}>
          <IoHeartSharp style={{ color: "red" }} />
          40
        </p> */}
        <p className="flex items-center gap-1 text-[15px] text-black/60">
          <IoHeartSharp style={{ color: "red" }} />
          {count}
        </p>
      </div>
    </div>
  )

}