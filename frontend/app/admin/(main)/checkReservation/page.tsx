import { redirect } from "next/navigation"

type Reservation = {
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
    imageUrl: '/images/poster1.png',
    likeCount: 24,
    reservations: [
      {
        username: '심오비',
        date: '25.05.16',
        time: '오후 6:00',
        people: 4,
      },
      {
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
    imageUrl: '/images/poster2.png',
    likeCount: 12,
    reservations: [
      {
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
    imageUrl: '/images/poster3.png',
    likeCount: 30,
    reservations: [
      {
        username: '이장군',
        date: '25.05.16',
        time: '오후 6:30',
        people: 5,
      },
    ],
  },
]


export default function CheckReservationPage() {
    const firstBoothId = mockAdminBooths[0].id
    redirect(`/admin/checkReservation/${firstBoothId}`)
  }