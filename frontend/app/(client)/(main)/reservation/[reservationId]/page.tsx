// // 예약 수정 페이지
// export default function EditReservationPage() {
//     return (
//       <div>
//         <h1>예약 수정</h1>
//         <p>예약 수정 페이지 입니다!</p>
//       </div>
//     );
//   }

// app/(client)/(main)/festival/[festivalId]/booth/[boothId]/reservations/page.tsx

'use client';

import { useState, useEffect } from 'react';
import { useParams } from 'next/navigation';
import { IoHeart } from 'react-icons/io5';
import Image from 'next/image';
import Header from '@/components/Headers';
import LikeButton from '@/components/LikeButton';
import { formatDate } from '@/libs/utils';

interface BoothType {
  boothId:number;
  id: number;
  name: string;
  host: string;
  location: string;
  description: string;
  startDateTime: string;
  endDateTime: string;
  likeCount: number;
  posterImageUrl: string;
  eventImageUrl: string;
  createdAt: string;
  updatedAt: string;
}

// interface BoothType {
//   boothId:number;
//   festivalId: number;
//   name: string;
//   host: string;
//   location: string;
//   description: string;
//   startDate: string;
//   endDate: string;
//   openTime: string;
//   closeTime: string;
//   likeCount: number;
//   posterImageUrl: string;
//   eventImageUrl: string;
//   createdAt: string;
//   updatedAt: string;
// }


interface FestivalType {
  id: number;
  posterImageUrl: string;
  mapImageUrl: string;
  name: string;
  startDate: string;
  endDate: string;
  location: string;
  description: string;
  likeCount: number;
  booths: BoothType[];
}

// interface FestivalType {
//   festivalId: number;
//   name: string;
//   startDate: string;
//   endDate: string;
//   location: string;
//   description: string;
//   likeCount: number;
//   booths: BoothType[];
// }



export default function BoothReservationPage() {
  const params = useParams();
  const festivalId = params?.festivalId as string;
  const boothId = params?.boothId as string;

  const [booth, setBooth] = useState<BoothType | null>(null);
  const [festival, setFestival] = useState<FestivalType | null>(null);
  const [selectedDate, setSelectedDate] = useState('05.16 (금) / 4명');
  const [selectedTime, setSelectedTime] = useState('18:30');
  const [numberOfPeople, setNumberOfPeople] = useState(4);
  const [paymentMethod, setPaymentMethod] = useState<'card' | 'bank'>('card');

  useEffect(() => {
    const fetchData = async () => {
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/festivals/${festivalId}`);
      const data: FestivalType = await res.json();
      const found = data.booths.find((b) => b.id === Number(boothId)) || null;
      setFestival(data);
      setBooth(found);
    };
    fetchData();
  }, [festivalId, boothId]);

  const handleReserve = async () => {
    const reservationBody = {
      userId: 1,
      boothId: Number(boothId),
      reservationTime: `2025-05-18T${selectedTime}:00`,
      numberOfPeople: numberOfPeople,
      // paymentMethod: paymentMethod,
    };

    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/reservations`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(reservationBody),
    });

    if (res.ok) alert('예약 완료');
    else alert('예약 실패');
  };

  if (!booth || !festival) return null;

  return (
    <div className="pb-32">
      <Header isBackButton={true} title={booth.name} />

      <div className="relative p-4 pt-16 space-y-6">
        <div className="relative w-full h-[270px]">
          <Image src={booth.posterImageUrl} alt="부스 포스터" fill className="object-cover" />
          <LikeButton initialLiked={false} size={25} className="absolute top-2 right-2" />
        </div>

        <div>
          <div className="flex items-center gap-2 mt-4">
            <h2 className="text-xl font-bold">{booth.name}</h2>
            <div className="flex items-center gap-1 text-[15px] text-black/60">
              <IoHeart size={18} className="text-red-500" />
              {booth.likeCount}
            </div>
          </div>
          <ul className="list-disc pl-5 text-sm space-y-1 mt-2">
            <li><strong>기간</strong> : {formatDate(booth.startDateTime)} - {formatDate(booth.endDateTime)}</li>
            <li><strong>위치</strong> : {booth.location}</li>
          </ul>
        </div>

        <div className="pt-4 border-t border-[#335533b3] space-y-2"></div>
        <div className="space-y-6">
          <div>
            <h3 className="text-lg font-bold">예약</h3>
            {/* <select
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              className="w-full mt-2 border border-gray-300 rounded-md p-2"
            >
              <option value="05.16 (금) / 4명">05.16 (금) / 4명</option>
              <option value="05.17 (토) / 4명">05.17 (토) / 4명</option>
            </select> */}
            <div className="flex gap-4 mt-2">
            {/* 날짜 선택 */}
            <select
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              className="w-1/2 border border-gray-300 rounded-md p-2"
            >
              <option value="2025-05-16">05.16 (금)</option>
              <option value="2025-05-17">05.17 (토)</option>
            </select>

            {/* 명수 선택 */}
            <select
              value={numberOfPeople}
              onChange={(e) => setNumberOfPeople(Number(e.target.value))}
              className="w-1/2 border border-gray-300 rounded-md p-2"
            >
              {[1, 2, 3, 4, 5, 6].map((num) => (
                <option key={num} value={num}>
                  {num}명
                </option>
              ))}
            </select>
          </div>

            {/* 시간 선택 - 슬라이드형 */}
            <div className="overflow-x-auto whitespace-nowrap mt-4">
              <div className="inline-flex gap-2 px-1">
                {['18:00', '18:30', '19:00', '19:30', '20:00', '20:30', '21:00'].map((time) => (
                  <button
                    key={time}
                    onClick={() => setSelectedTime(time)}
                    className={`min-w-[110px] h-[45px] text-[15px] border rounded-md font-medium ${
                      selectedTime === time ? 'bg-[#335533] text-white' : 'bg-white text-black border-gray-300'
                    }`}
                  >
                    오후 {time}
                  </button>
                ))}
              </div>
            </div>
          </div>

          <div>
            <h3 className="text-lg font-bold">결제 수단</h3>
            <div className="w-full h-[140px] rounded-[10px] border border-gray-200 bg-white flex flex-col justify-center items-center gap-2 shadow-sm">
              {[
                { value: 'card', label: '카드' },
                { value: 'bank', label: '계좌 이체' },
              ].map((option, idx) => (
                <div key={option.value} className="flex items-center justify-between w-[80%] h-[50%]">
                  <span className="text-sm font-medium">{option.label}</span>
                  <div
                    onClick={() => setPaymentMethod(option.value as 'card' | 'bank')}
                    className={`w-[16px] h-[16px] rounded-full border-[2px] cursor-pointer ${
                      paymentMethod === option.value ? 'bg-[#335533]' : 'bg-white'
                    } border-[#335533]`}
                  />
                  {idx === 0 && <div className="absolute left-1/2 -translate-x-1/2 w-[75%] h-[1px] bg-[#335533b3] mt-[70px]" />}
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      <div className="fixed bottom-0 left-0 w-full py-4 bg-white border-t border-gray-200 z-50 flex justify-center">
        <button
          onClick={handleReserve}
          className="w-[289px] h-[48px] bg-[#335533] text-white font-bold text-[20px] rounded-lg"
        >
          수정 완료
        </button>
      </div>
    </div>
  );
}