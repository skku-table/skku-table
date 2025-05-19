'use client'
import { IoHeart } from 'react-icons/io5';
import Header from "@/components/Headers"
import { redirect } from "next/navigation"
import Image from "next/image"

import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
  } from "@/components/ui/select"



type Reservation = {
    reservationId: number
    username: string
    date: string // 예: '25.05.16'
    time: string // 예: '오후 6:00'
    people: number
  }
  
type Booth = {
    id: number
    name: string
    location: string
    period: string
    imageUrl: string
    likeCount: number
    reservations: Reservation[]
}

type AdminBoothList = Booth[]
  
const mockAdminBooths: AdminBoothList = [
  {
    id: 1,
    name: '다같이 추억 숲으로',
    location: '삼성학술정보관 앞 잔디밭 3번 부스',
    period: '5.16 - 5.17',
    imageUrl: '/src/booth1.png',
    likeCount: 24,
    reservations: [
      {
        reservationId:10,
        username: '심오비',
        date: '25.05.16',
        time: '오후 6:00',
        people: 4,
      },
      {
        reservationId:11,
        username: '타메르',
        date: '25.05.17',
        time: '오후 7:30',
        people: 2,
      },
    ],
  },
  {
    id: 2,
    name: 'Hotel AKDONG',
    location: '율전 캠퍼스 중앙광장',
    period: '5.16 - 5.17',
    imageUrl: '/src/booth2.png',
    likeCount: 12,
    reservations: [
      {
        reservationId:12,
        username: '유세윤',
        date: '25.05.17',
        time: '오후 5:00',
        people: 3,
      },
    ],
  },
  {
    id: 3,
    name: '술이술이 마술이',
    location: '제2과학관 옆 잔디밭',
    period: '5.16 - 5.17',
    imageUrl: '/src/booth3.png',
    likeCount: 30,
    reservations: [
      {
        reservationId:13,
        username: '이장군',
        date: '25.05.16',
        time: '오후 6:30',
        people: 5,
      },
    ],
  },
]

export default function CheckReservationDetail({params}: {params:{boothId: string}}) {
    const boothId = Number(params.boothId);
    const booth:Booth = mockAdminBooths.find((booth) => booth.id === Number(boothId)) as Booth;
    function redirectBoothId(boothId: number) {
        redirect(`/admin/checkReservation/${boothId}`);
    }


    return (
      <div>
        <Header isBackButton={false} title="Check Reservation" />
        <div className="relative p-4 pt-16 space-y-6">
            <Select>
                <SelectTrigger className="w-[180px]">
                    <SelectValue placeholder={booth.name} />
                </SelectTrigger>
                <SelectContent>
                    {mockAdminBooths.map((booth) => (
                        <SelectItem key={booth.id} value={booth.name} onClick={() => redirectBoothId(booth.id)}>
                            {booth.name}
                        </SelectItem>
                    ))}
                </SelectContent>
            </Select>
            <Image
                src={booth.imageUrl}
                alt="부스 포스터"
                width={312}
                height={312}
              />
            <div>
              <div className="flex items-center gap-2 mt-4">
                <h2 className="text-xl font-bold">{booth.name}</h2>
                <div className="flex items-center gap-1 text-[15px] text-black/60">
                  <IoHeart size={18} className="text-red-500" />
                  {booth.likeCount}
                </div>
              </div>
              <ul className="list-disc pl-5 text-sm space-y-1 mt-2">
                <li><strong>기간</strong> : {booth.period}</li>
                <li><strong>위치</strong> : {booth.location}</li>
              </ul>
            </div>
            <div className="pt-4 border-t border-[#335533b3] space-y-2">
              <h1 className="text-xl font-bold mb-5">예약 현황</h1>
              <div className="text-sm space-y-1">
                {booth.reservations.map((reservation) => (
                  <div key={reservation.reservationId}>
                      <hr className="my-2 border-t border-gray-300" />
                      <div className='flex justify-between content-center items-center'>
                        <div>
                          <p className='text-lg font-bold mb-2'>{reservation.username}</p>
                          <ul>
                            <li><strong>예약일</strong> : {reservation.date}</li>
                            <li><strong>예약시간</strong> : {reservation.time}</li>
                            <li><strong>예약인원</strong> : {reservation.people}</li>
                          </ul>
                        </div>
                        <button className="bg-red-500 text-white w-20 h-8 rounded-md mt-2">
                        <p>예약 취소</p>
                        </button>
                      </div>
                      <hr className="my-2 border-t border-gray-300" />
                    
                  </div>
                ))}
              </div>
            </div>
        </div>
      </div>
    );
  }