// 예약 하기 페이지 !!

// app/(client)/(main)/festival/[festivalId]/booth/[boothId]/reservations/page.tsx

'use client';

import { useState, useEffect } from 'react';
import { useParams } from 'next/navigation';
import { formatDate } from '@/libs/utils';
import DetailHeader from '@/components/DetailHeader';
import { formatToKoreanTime } from '@/libs/utils';
import { fetchWithCredentials } from '@/libs/fetchWithCredentials';
import { useRouter } from 'next/navigation'; 
import { getToken } from 'firebase/messaging';
import { messaging } from '@/libs/firebase';

interface BoothType {
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
}
interface FestivalType {
  id: number;
  booths: BoothType[];
}

// 운영 날짜 정보를 받아 해당 범위에 있는 모든 날짜 리스트를 생성
function getDateRange(start: string, end: string): string[] {
  const result: string[] = [];
  const startDate = new Date(start);
  const endDate = new Date(end);

  for (let d = new Date(startDate); d <= endDate; d.setDate(d.getDate() + 1)) {
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    result.push(`${yyyy}-${mm}-${dd}`);
  }
  return result;
}

// 부스 운영 시간 정보를 받아 해당 범위에 있는 시간 리스트 생성
function generateTimeSlotsFromTimes(start: string, end: string): string[] {
  const result: string[] = [];

  const startTime = new Date(`1970-01-01T${start}`);
  const endTime = new Date(`1970-01-01T${end}`);

  const current = new Date(startTime);
  while (current <= endTime) {
    const hh = current.getHours().toString().padStart(2, '0');
    const mm = current.getMinutes().toString().padStart(2, '0');
    result.push(`${hh}:${mm}`);
    current.setMinutes(current.getMinutes() + 30);
  }

  return result;
}

export default function BoothReservationPage() {
  const router = useRouter();
  const params = useParams();
  const festivalId = params?.festivalId as string;
  const boothId = params?.boothId as string;

  const [booth, setBooth] = useState<BoothType | null>(null);
  const [festival, setFestival] = useState<FestivalType | null>(null);
  const [selectedDate, setSelectedDate] = useState<string>('');
  const [selectedTime, setSelectedTime] = useState('');
  const [numberOfPeople, setNumberOfPeople] = useState(0);
  const [paymentMethod, setPaymentMethod] = useState<'CARD' | 'BANK'>('CARD');
  const [dateList, setDateList] = useState<string[]>([]);
  const [timeList, setTimeList] = useState<string[]>([]);
  const [userId, setUserId] = useState<number | null>(null);



  useEffect(() => {
  const fetchUserInfo = async () => {
    try {
      const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/users/me`);
      const contentType = res.headers.get('content-type');
      if (!contentType?.includes('application/json')) {
        const text = await res.text();
        console.error('유저 정보 응답이 JSON이 아님:', text);
        return;
      }
      const userData = await res.json();
      setUserId(userData.id);
    } catch (err) {
      console.error('유저 정보 불러오기 실패:', err);
    }
  };

  fetchUserInfo();
}, []);


  useEffect(() => {
    const fetchData = async () => {
        try {
        const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/festivals/${festivalId}`);
        const data: FestivalType = await res.json();

        const foundBooth = data.booths.find((b) => b.id === Number(boothId));
        setFestival(data);
        setBooth(foundBooth ?? null);

        if (foundBooth) {
          setBooth(foundBooth);
          const fullDateList = getDateRange(foundBooth.startDateTime, foundBooth.endDateTime);
          setSelectedDate(fullDateList[0]);
          setDateList(fullDateList);

          const startTimeStr = foundBooth.startDateTime.split('T')[1].slice(0, 5); // "HH:mm"
          const endTimeStr = foundBooth.endDateTime.split('T')[1].slice(0, 5);     // "HH:mm"
          const timeList = generateTimeSlotsFromTimes(startTimeStr, endTimeStr);
          setSelectedTime(timeList[0]);
          setTimeList(timeList);
        }
        } catch (error) {
        console.error('데이터 불러오기 실패:', error);
        }
    };
    fetchData();
    }, [festivalId, boothId]);

  const handleReserve = async () => {

    if (!userId) {
    alert('로그인이 필요합니다.');
    return;
  }

    // 예외 처리
    if (!selectedDate || !selectedTime || numberOfPeople <= 0) {
    alert('날짜, 시간, 인원을 모두 선택해주세요.');
    return;
    }

    let fcmToken = '';
    if (messaging) {
      try {
        fcmToken = await getToken(messaging, {
          vapidKey: process.env.NEXT_PUBLIC_FIREBASE_VAPID_PUBLIC_KEY!,
        });
        console.log('✅ 예약용 FCM Token:', fcmToken);
      } catch (error) {
        console.error('❌ FCM 토큰 가져오기 실패:', error);
      }
    } else {
      console.warn('⚠️ Firebase Messaging이 초기화되지 않았습니다.');
      // 필요에 따라 early return
    }

    const reservationBody = {
      userId: Number(userId),
      boothId: Number(boothId),
      festivalId: Number(festivalId),
      reservationTime: `${selectedDate}T${selectedTime}:00`,
      numberOfPeople: numberOfPeople,
      paymentMethod: paymentMethod,
      fcmToken: fcmToken
    };

    const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/reservations`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(reservationBody),
    });


    if (res.ok) {
      const confirmed = window.confirm('예약 완료');
      if (confirmed) {
        router.push('/'); // 메인 화면 경로로 이동
      }
    } else {
      const errorText = await res.text();
      console.error('예약 실패 응답:', errorText);
      alert('예약 실패');
    }
  };

  if (!booth || !festival) return null;

  return (
    <div className="pb-32 px-4">
        <DetailHeader
            name={booth.name}
            startDate={booth.startDateTime}
            endDate={booth.endDateTime}
            location={booth.location}
            posterImageUrl={booth.posterImageUrl}
            likeCount={booth.likeCount}
            boothId={booth.id}
        />
        <div className="relative p-4 space-y-6">
            <div>
            <div className="w-full h-px bg-[#335533b3] mx-auto mb-6" />
            <h3 className="text-lg font-bold">예약</h3>
            <div className="flex gap-4 mt-2">
                <select
                value={selectedDate}
                onChange={(e) => setSelectedDate(e.target.value)}
                className="w-1/2 border border-gray-300 rounded-md p-2"
                >
                {dateList.map((date) => (
                    <option key={date} value={date}>
                    {formatDate(date)} 
                    </option>
                ))}
                </select>

                <select
                value={numberOfPeople}
                onChange={(e) => setNumberOfPeople(Number(e.target.value))}
                className="w-1/2 border border-gray-300 rounded-md p-2"
                >
                {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((num) => (
                    <option key={num} value={num}>{num}명</option>
                ))}
                </select>
            </div>

            <div className="overflow-x-auto whitespace-nowrap mt-4">
                <div className="inline-flex gap-2 px-1">
                {timeList.map((time) => (
                  <button
                    key={time}
                    onClick={() => setSelectedTime(time)}
                    className={`min-w-[110px] h-[45px] text-[15px] border rounded-md font-medium ${
                      selectedTime === time ? 'bg-[#335533] text-white' : 'bg-white text-black border-gray-300'
                    }`}
                  >
                    {formatToKoreanTime(time)}
                  </button>
                ))}
                </div>
            </div>
            </div>

            <div>
            <div className="w-[95%] h-px bg-[#335533b3] mx-auto mb-6" />
            <h3 className="text-lg font-bold">결제 수단</h3>
            <div className="mt-3 w-full h-[140px] rounded-[10px] border border-gray-200 bg-white flex flex-col justify-center items-center gap-2 shadow-sm">
                {[
                { value: 'CARD', label: '카드' },
                { value: 'BANK', label: '계좌 이체' },
                ].map((option, idx) => (
                <div key={option.value} className="flex items-center justify-between w-[80%] h-[50%]">
                    <span className="text-sm font-medium">{option.label}</span>
                    <div
                    onClick={() => setPaymentMethod(option.value as 'CARD' | 'BANK')}
                    className={`w-[16px] h-[16px] rounded-full border-[2px] cursor-pointer ${
                        paymentMethod === option.value ? 'bg-[#335533]' : 'bg-white'
                    } border-[#335533]`}
                    />
                    {idx === 0 && (
                    <div className="absolute left-1/2 -translate-x-1/2 w-[75%] h-[1px] bg-[#335533b3] mt-[70px]" />
                    )}
                </div>
                ))}
            </div>
            </div>
        </div>

    <div className="fixed bottom-0 left-0 w-full py-4 bg-white border-t border-gray-200 z-50 flex justify-center">
        <button
        onClick={handleReserve}
        className="w-[289px] h-[48px] bg-[#335533] text-white font-bold text-[20px] rounded-lg"
        >
        예약 완료
        </button>
    </div>
    </div>
    );
}