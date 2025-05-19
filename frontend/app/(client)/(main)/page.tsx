'use client';

import Image from "next/image";
import LikeButton from "@/components/LikeButton";
import { IoHeartSharp } from "react-icons/io5";
import { useState } from "react";
import Link from "next/link";
import Header from "@/components/Headers"


const mockFestivals = [
  {
    festivalId: 1,
    name: '2025 성균관대 대동제',
    startDate: '2025-05-15',
    endDate: '2025-05-16',
    location: '율전 캠퍼스',
    description: '율전대동제',
    likeCount : 40
  },
  {
    festivalId: 2,
    name: '2025 고려대 대동제',
    startDate: '2025-05-10',
    endDate: '2025-10-12',
    location: '안암 캠퍼스',
    description: '고려대 대동제',
    likeCount : 30
  },
];



// 날짜 포맷: "2025-05-15" → "5.15"
const formatDate = (date: string) => {
  const [ , month, day] = date.split('-');
  return `${parseInt(month)}.${parseInt(day)}`;
};

export default function Page() {
  // 상태로 변경
  const [festivals, setFestivals] = useState(
    mockFestivals.map(f => ({ ...f, liked: false }))
  );

  // 좋아요 토글 함수
  const toggleLike = (festivalId: number) => {
    setFestivals(prevFestivals =>
      prevFestivals.map(festival =>
        festival.festivalId === festivalId
          ? {
              ...festival,
              liked: !festival.liked,
              likeCount: festival.liked
                ? festival.likeCount - 1
                : festival.likeCount + 1,
            }
          : festival
      )
    );
  };

  return (
    <>
      <Header isBackButton={false} title="Current Festivals"/>
      <main className="pt-[72px] p-4">
        {/* <h1 className="text-xl font-extrabold mb-4 sticky top-0 bg-white z-10 py-3">진행 중인 축제</h1> */}

        <div className="flex flex-col items-center gap-6">
          {festivals.map((festival) => (
            <div key={festival.festivalId}>
              {/* <div className="relative w-[290px] h-[290px] mx-auto">
              <Link href={`/festival/${festival.festivalId}`}>
                <Image
                  src="/tmp/skku_festival2.png" // 이거 축제 이미지 불러오는 건 나중에 따로 구현
                  alt="festival poster"
                  fill
                  className="rounded-xl object-cover"
                />
                </div>
                </Link>  
                <LikeButton
                  initialLiked={festival.liked}
                  size={25}
                  onClick={() => toggleLike(festival.festivalId)}
                /> */}
                <div className="relative w-[290px] h-[290px] mx-auto">
                  <Link href={`/festival/${festival.festivalId}`}>
                    <Image
                      src="/tmp/skku_festival2.png"
                      alt="festival poster"
                      fill
                      className="rounded-xl object-cover cursor-pointer"
                    />
                  </Link>
                  <LikeButton
                    initialLiked={festival.liked}
                    size={25}
                    onClick={() => toggleLike(festival.festivalId)}
                  />
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
                  {festival.likeCount}
                </p>
              </div>
            </div>
          ))}
        </div>

      </main>
    </>
  );
}
