'use client'
import { fetchWithCredentials } from '@/libs/fetchWithCredentials';
import { formatDate, formatTime } from '@/libs/utils';
import { useState } from 'react';

type reservations ={
    reservationId: number;
    userId: number;
    userName: string;
    reservationTime: string;
    numberOfPeople: number;
    paymentMethod: string;
    createdAt: string;
  };
export default function CheckReservationCard({ reservation }: { reservation: reservations[] }) {
    
    const [reservations, setReservations] = useState(reservation);
    
      
    const handleCancel = async (reservationId: number) => {
        const isConfirmed = window.confirm('정말 취소하시겠습니까?');
        if (!isConfirmed) return;
    
        try {
          const response = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/v2/reservations/${reservationId}`, {
            method: 'DELETE',
          });
    
          if (!response.ok) {
            throw new Error('서버 응답이 실패했습니다.');
          }
    
          // 성공적으로 삭제됐으면 화면에서도 제거
          setReservations(prev =>
            prev.filter(reservation => reservation.reservationId !== reservationId)
          );
        } catch (error) {
          alert('예약 취소에 실패했습니다.');
          console.error(error);
        }
    };
    
    return (
        <div>
          {reservations.map((reservation) => (
            <div key={reservation.reservationId}>
              <hr className="my-2 border-t border-gray-300" />
              <div className='flex justify-between content-center items-center'>
                <div>
                  <p className='text-lg font-bold mb-2'>{reservation.userName}</p>
                  <ul>
                    <li><strong>예약일</strong> : {formatDate(reservation.reservationTime)}</li>
                    <li><strong>예약시간</strong> : {formatTime(reservation.reservationTime)}</li>
                    <li><strong>예약인원</strong> : {reservation.numberOfPeople}</li>
                  </ul>
                </div>
                <button
                  onClick={() => handleCancel(reservation.reservationId)}
                  className="bg-red-500 text-white w-20 h-8 rounded-md mt-2 hover:cursor-pointer"
                >
                  <p>예약 취소</p>
                </button>
              </div>
              <hr className="my-2 border-t border-gray-300" />
            </div>
          ))}
        </div>
      )

}