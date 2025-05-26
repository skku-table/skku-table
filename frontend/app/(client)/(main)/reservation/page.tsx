'use client';

import { useEffect, useState } from 'react';
import Image from 'next/image';
import cn from 'classnames';
import { formatToKoreanTime } from '@/libs/utils';
import Link from 'next/link';

interface Reservation {
  reservationId: number;
  boothId: number;
  boothName: string;
  festivalName: string;
  reservationTime: string;
  numberOfPeople: number;
  boothPosterImageUrl: string;
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  const mm = date.getMonth() + 1;
  const dd = date.getDate();
  const weekday = ['일', '월', '화', '수', '목', '금', '토'][date.getDay()];
  return `${mm}.${dd} ${weekday}`;
}

export default function ReservationPage() {

  const [tab, setTab] = useState<'current' | 'past'>('current');
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [userId, setUserId] = useState<number | null>(null);

    // 사용자 정보 먼저 불러오기
  useEffect(() => {
    async function fetchUserInfo() {
      try {
        const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/users/me`, {
          credentials: 'include', // 쿠키 인증 필요 

          headers: {
            'Content-Type': 'application/json',
          },
        });

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
        const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/reservations/user/${userId}`);
        const data = await res.json();
        setReservations(data);
      } catch (error) {
        console.error('예약 정보 불러오기 실패:', error);
      }
    }
    fetchReservations();
  }, [userId]);

  const now = new Date();
  const filtered = reservations.filter((r) => {
    const reservationDate = new Date(r.reservationTime);
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
        const timeStr = new Date(r.reservationTime).toTimeString().slice(0, 5);
        const formattedTime = formatToKoreanTime(timeStr);

        return (
          <div key={r.reservationId} className="mb-6">
            <p className="font-bold text-[17px] mb-2">
              {formatDate(r.reservationTime)}
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

                {/* 수정 및 취소 버튼: current 탭에서만 보여짐 */}
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
                          const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/reservations/${r.reservationId}`, {
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