'use client';

import { useEffect, useState } from 'react';
import Image from 'next/image';
import cn from 'classnames';
import { formatToKoreanTime } from '@/libs/utils';
import Link from 'next/link';
import { fetchWithCredentials } from '@/libs/fetchWithCredentials';

interface Reservation {
  reservationId: number;
  boothId: number;
  boothName: string;
  festivalName: string;
  timeSlotStartTime: string;
  timeSlotEndTime: string;
  reservationTime: string;
  numberOfPeople: number;
  boothPosterImageUrl: string;
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  const yy = String(date.getFullYear()).slice(2);
  const mm = date.getMonth() + 1;
  const dd = date.getDate();
  const weekday = ['일', '월', '화', '수', '목', '금', '토'][date.getDay()];
  
  return `${yy}.${mm}.${dd} ${weekday}`;
}

function safeDate(dateStr: string): Date {
  return new Date(dateStr.includes(':00') ? dateStr : `${dateStr}:00`);
}

export default function ReservationPage() {

  const [tab, setTab] = useState<'current' | 'past'>('current');
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [userId, setUserId] = useState<number | null>(null);


    // 사용자 정보 먼저 불러오기
  useEffect(() => {
    async function fetchUserInfo() {
      try {
        const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/users/me`, {
          headers: {
            'Content-Type': 'application/json',
          },
        });
        const contentType = res.headers.get("content-type");
        if (!contentType?.includes("application/json")) {
          const text = await res.text();
          console.error("JSON 아님 - 서버 응답:", text);
          throw new Error("JSON 파싱 불가");
        }

        const data = await res.json();
        setUserId(data.id);
      } catch (error) {
        console.error('유저 정보 불러오기 실패:', error);
      }
    }

    fetchUserInfo();
  }, []);

  useEffect(() => {
    if (userId === null) return;

    async function fetchReservations() {
      try {

        const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/v2/reservations/my`);
        const data = await res.json();
        setReservations(data);
      } catch (error) {
        // console.log("user Id : ", userId);
        console.error('예약 정보 불러오기 실패:', error);
      }
    }
    fetchReservations();
  }, [userId]);

  const now = new Date();
  const filtered = reservations.filter((r) => {
    const reservationDate = new Date(r.timeSlotStartTime);
    return tab === 'current' ? reservationDate >= now : reservationDate < now;
  });

  return (
    <div className="p-4 pb-24">
      <h1 className="text-xl font-bold mb-4">Reservation</h1>

      {/* 탭 */}
      <div className="flex mb-4 border-b">
        {['current', 'past'].map((type) => (
          <button
            key={type}
            onClick={() => setTab(type as 'current' | 'past')}
            className={cn(
              'w-1/2 py-2 font-bold text-xl text-black',
              {
                'border-b-2 border-[#335533]': tab === type,
              }
            )}
          >
            {type === 'current' ? '예약 내역' : '지난 예약'}
          </button>
        ))}
      </div>

      {/* 예약 카드 목록 */}
     {filtered.map((r) => {
        const dateObj = safeDate(r.timeSlotStartTime);
        const hours = dateObj.getHours().toString().padStart(2, '0');
        const minutes = dateObj.getMinutes().toString().padStart(2, '0');
        const formattedTime = formatToKoreanTime(`${hours}:${minutes}`);

        return (
          <div key={r.reservationId} className="mb-6">
            <p className="font-bold text-[17px] mb-2">
                {formatDate(r.timeSlotStartTime.includes(':00') ? r.timeSlotStartTime : `${r.timeSlotStartTime}:00`)}
            </p>
            <div className="flex gap-4 items-center">
              <Image
                src={r.boothPosterImageUrl}
                alt={r.boothName}
                width={80}
                height={80}
                className="object-cover w-[130px] h-[130px] rounded-[10%]"
              />
              <div className="flex-1">
                <p className="font-bold text-lg mb-1">{r.boothName}</p>
                <ul className="text-base text-black">
                  <li>ㆍ{r.festivalName}</li>
                  <li>ㆍ{formattedTime} / {r.numberOfPeople}명</li>
                </ul>

                {tab === 'current' && (
                  <div className="flex gap-3 mt-2">
                    <Link href={`/reservation/${r.reservationId}`}>
                      <button className="font-bold w-[90px] h-[28px] rounded-full bg-[#3355331A] text-xs text-black active:bg-[#335533] active:text-white">
                        예약 수정
                      </button>
                    </Link>
                    <button
                      className="font-bold w-[90px] h-[28px] rounded-full bg-[#3355331A] text-xs text-black active:bg-[#335533] active:text-white"
                      onClick={async () => {
                        const confirmed = window.confirm('정말 예약을 취소하시겠습니까?');
                        if (!confirmed) return;
                        try {
                          const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/v2/reservations/${r.reservationId}`, {
                            method: 'DELETE',
                          });
                          if (res.ok) {
                            setReservations(prev => prev.filter(item => item.reservationId !== r.reservationId));
                            alert('예약이 취소되었습니다');
                          } else {
                            alert('예약 취소에 실패했습니다');
                          }
                        } catch (err) {
                          console.error(err);
                          alert('오류가 발생했습니다');
                        }
                      }}
                    >
                      예약 취소
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        );
      })}

    </div>
  );
}