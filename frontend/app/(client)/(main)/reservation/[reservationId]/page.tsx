'use client';

import { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { formatDate, formatToKoreanTime } from '@/libs/utils';
import DetailHeader from '@/components/DetailHeader';
import { fetchWithCredentials } from '@/libs/fetchWithCredentials';
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

interface ReservationType {
  reservationId: number;
  userId: number;
  userName: string;
  boothId: number;
  boothName: string;
  boothStartDate: string;
  boothPosterImageUrl: string;
  reservationTime: string;
  festivalId: number;
  festivalName: string;
  numberOfPeople: number;
  paymentMethod: 'CARD' | 'BANK';
  createdAt: string;
  timeSlotId: number;
  timeSlotStartTime: string;
  timeSlotEndTime: string;
}

interface TimeSlot {
  id: number;
  startTime: string;
  endTime: string;
  availableCapacity: number;
}

export default function EditReservationPage() {
  const router = useRouter();
  const params = useParams();
  const reservationId = params?.reservationId as string;
  const [booth, setBooth] = useState<BoothType | null>(null);
  const [selectedDate, setSelectedDate] = useState('');
  const [selectedTime, setSelectedTime] = useState('');
  const [numberOfPeople, setNumberOfPeople] = useState(0);
  const [paymentMethod, setPaymentMethod] = useState<'CARD' | 'BANK'>('CARD');
  const [dateList, setDateList] = useState<string[]>([]);
  const [timeSlots, setTimeSlots] = useState<TimeSlot[]>([]);

  const filteredTimeSlots = timeSlots.filter((slot) => slot.startTime.startsWith(selectedDate));

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/v2/reservations/my`);
        const reservations: ReservationType[] = await res.json();
        const currentReservation = reservations.find((r) => r.reservationId === Number(reservationId));

        if (!currentReservation) {
          alert('해당 예약 정보를 찾을 수 없습니다.');
          return;
        }

        setSelectedDate(currentReservation.timeSlotStartTime.split('T')[0]);
        setSelectedTime(currentReservation.timeSlotStartTime.split('T')[1].slice(0, 5));
        setNumberOfPeople(currentReservation.numberOfPeople);
        setPaymentMethod(currentReservation.paymentMethod);

        const boothRes = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/festivals/${currentReservation.festivalId}`);
        const festivalData: FestivalType = await boothRes.json();
        const foundBooth = festivalData.booths.find((b) => b.id === currentReservation.boothId);
        setBooth(foundBooth ?? null);

        if (foundBooth) {
          const timeSlotRes = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/booths/${foundBooth.id}/timeslots/available`);
          const timeSlotData: TimeSlot[] = await timeSlotRes.json();
          setTimeSlots(timeSlotData);

          const uniqueDates = Array.from(new Set(timeSlotData.map((slot) => slot.startTime.split('T')[0])));
          setDateList(uniqueDates);
        }
      } catch (err) {
        console.error('수정 예약 데이터 불러오기 실패:', err);
      }
    };
    fetchData();
  }, [reservationId]);

  const handleEdit = async () => {
    if (!selectedDate || !selectedTime || numberOfPeople <= 0) {
      alert('날짜, 시간, 인원을 모두 선택해주세요.');
      return;
    }

    const matchedSlot = filteredTimeSlots.find((slot) => {
      const time = slot.startTime.split('T')[1].slice(0, 5);
      return time === selectedTime;
    });

    if (!matchedSlot) {
      alert('해당 시간 슬롯을 찾을 수 없습니다.');
      return;
    }

    let fcmToken = '';
    if (messaging) {
      try {
        fcmToken = await getToken(messaging, {
          vapidKey: process.env.NEXT_PUBLIC_FIREBASE_VAPID_PUBLIC_KEY!,
        });
        console.log('✅ 수정용 FCM Token:', fcmToken);
      } catch (error) {
        console.error('❌ FCM 토큰 가져오기 실패:', error);
      }
    }

    const body = {
      timeSlotId: matchedSlot.id,
      numberOfPeople,
      paymentMethod,
      fcmToken
    };

    const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/v2/reservations/${reservationId}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    });

    if (res.ok) {
      alert('예약이 수정되었습니다.');
      router.push('/');
    } else {
      alert('수정에 실패했습니다.');
    }
  };

  if (!booth) return null;

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
          <h3 className="text-lg font-bold">예약 수정</h3>
          <div className="flex gap-4 mt-2">
            <select
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              className="w-1/2 border border-gray-300 rounded-md p-2"
            >
              {dateList.map((date) => (
                <option key={date} value={date}>{formatDate(date)}</option>
              ))}
            </select>
            <select
              value={numberOfPeople}
              onChange={(e) => setNumberOfPeople(Number(e.target.value))}
              className="w-1/2 border border-gray-300 rounded-md p-2"
            >
              {[1,2,3,4,5,6,7,8,9,10].map((num) => (
                <option key={num} value={num}>{num}명</option>
              ))}
            </select>
          </div>
          <div className="overflow-x-auto whitespace-nowrap mt-4">
            <div className="inline-flex gap-2 px-1">
              {filteredTimeSlots.map((slot) => {
                const time = slot.startTime.split('T')[1].slice(0, 5);
                return (
                  <button
                    key={slot.id}
                    onClick={() => setSelectedTime(time)}
                    className={`min-w-[110px] h-[45px] text-[15px] border rounded-md font-medium ${
                      selectedTime === time ? 'bg-[#335533] text-white' : 'bg-white text-black border-gray-300'
                    }`}
                  >
                    {formatToKoreanTime(time)}
                  </button>
                );
              })}
            </div>
          </div>
        </div>
        <div>
          <div className="w-[95%] h-px bg-[#335533b3] mx-auto mb-6" />
          <h3 className="text-lg font-bold">결제 수단</h3>
          <div className="mt-3 w-full h-[140px] rounded-[10px] border border-gray-200 bg-white flex flex-col justify-center items-center gap-2 shadow-sm">
            {[{ value: 'CARD', label: '카드' }, { value: 'BANK', label: '계좌 이체' }].map((option, idx) => (
              <div key={option.value} className="flex items-center justify-between w-[80%] h-[50%]">
                <span className="text-sm font-medium">{option.label}</span>
                <div
                  onClick={() => setPaymentMethod(option.value as 'CARD' | 'BANK')}
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
      <div className="fixed bottom-0 left-0 w-full py-4 bg-white border-t border-gray-200 z-50 flex justify-center">
        <button
          onClick={handleEdit}
          className="w-[289px] h-[48px] bg-[#335533] text-white font-bold text-[20px] rounded-lg"
        >
          수정 완료
        </button>
      </div>
    </div>
  );
}
