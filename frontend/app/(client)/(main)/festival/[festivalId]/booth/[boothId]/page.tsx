'use client';

import { IoHeart } from 'react-icons/io5';
import Image from 'next/image';
import Header from '@/components/Headers';
import { useState } from 'react';
import LikeButton from '@/components/LikeButton';


const mockBoothDetail = {
  boothId: 1,
  festivalId: 1,
  name: '다같이 추억 솦으로',
  host: '소프트웨어학과',
  location: '삼성학술정보관 앞 잔디밭 3번 부스',
  description: '1인 입장 가능 <br /> 최대 4명까지 예약 가능',
  openTime: '17:00',
  closeTime: '23:00',
  startDate: '2025-05-16',
  endDate: '2025-05-17',
  likeCount: 24,
  posterImageUrl: '/booth1.png',
  eventImageUrl: '/booth1_event1.JPG',
};

//날짜 포멧 함수
function formatDateRange(start: string, end: string): string {
  const format = (dateStr: string) => {
    const [, month, day] = dateStr.split('-');
    return `${Number(month)}.${Number(day)}`; 
  };
  return `${format(start)} - ${format(end)}`;
}

export default function BoothDetailPage() {
  const booth = mockBoothDetail;
  const [liked, setLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(booth.likeCount);

  const toggleLike = () => {
    setLiked(prev => !prev);
    setLikeCount(prev => prev + (liked ? -1 : 1));
  };

  return (
    <>
      <Header isBackButton={true} title={booth.name} />
      <div className="relative p-4 pt-16 space-y-6">

        {/* 포스터 이미지 + 하트 버튼 */}
        <div className="relative w-full h-[270px]">
          <Image
            src={booth.posterImageUrl}
            alt="부스 포스터"
            fill
            className="object-cover"
          />
          <LikeButton
            initialLiked={liked}
            size={25}
            onClick={toggleLike}
            className="absolute top-2 right-2"
          />
        </div>

        {/* 부스 기본 정보 */}
        <div>
          <div className="flex items-center gap-2 mt-4">
            <h2 className="text-xl font-bold">{booth.name}</h2>
            <div className="flex items-center gap-1 text-[15px] text-black/60">
              <IoHeart size={18} className="text-red-500" />
              {likeCount}
            </div>
          </div>
          <ul className="list-disc pl-5 text-sm space-y-1 mt-2">
            <li><strong>기간</strong> : {formatDateRange(booth.startDate, booth.endDate)}</li>
            <li><strong>위치</strong> : {booth.location}</li>
          </ul>
        </div>

        {/* 부스 상세 정보 */}
        <div className="pt-4 border-t border-[#335533b3] space-y-2">
          <h1 className="text-xl font-bold">상세 정보</h1>
          <ul className="list-disc pl-5 text-sm space-y-1">
            {booth.description.split('<br />').map((item, idx) => (
              <li key={idx}>{item.trim()}</li>
            ))}
          </ul>
        </div>


        {/* 이벤트 배너 이미지 */}
        <Image
          src={booth.eventImageUrl}
          alt="이벤트"
          width={500}
          height={300}
          className="w-full object-cover rounded-lg"
        />

        {/* 예약 버튼 */}
        <div className="fixed bottom-0 left-0 w-full py-4 bg-white border-t border-gray-200 z-50 flex justify-center">
          <button className="w-[289px] h-[48px] bg-[#335533] text-white font-bold text-[20px] rounded-lg">
            예약하기
          </button>
        </div>

      </div>
    </>
  );
}