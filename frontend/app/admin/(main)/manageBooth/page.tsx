'use client'
import Image from 'next/image'
import { useRouter } from 'next/navigation'
import Header from '@/components/Headers'

type MyBooth = {
  id: number
  name: string
  posterImageUrl: string
  location: string
  date: string
}

export const mockMyBooths: MyBooth[] = [
  {
    id: 1,
    name: '다같이 추억 숲으로',
    posterImageUrl: '/src/booth1.png',
    location: '성균관대학교 낭만록',
    date: '2025.05.16',
  },
  {
    id: 2,
    name: 'HOTEL AKDONG',
    posterImageUrl: '/src/booth2.png',
    location: '성균관대학교 낭만록',
    date: '2025.05.17',
  },
  {
    id: 3,
    name: '술이술이 마술이',
    posterImageUrl: '/src/booth3.png',
    location: '성균관대학교 낭만록',
    date: '2025.05.17',
  },
]


export default function AdminBoothManagePage() {
  const router = useRouter()

  const handleBoothClick = (id: number) => {
    router.push(`/admin/checkReservation/${id}`)
  }

  const handleRegisterClick = () => {
    router.push('/admin/registerBooth')
  }

  return (
    <div>
      <Header isBackButton={false} title="Manage" />
      <div className="relative p-4 pt-16 space-y-6">
        <h3 className="text-lg font-bold mb-4">나의 부스</h3>
        <ul className="space-y-5">
          {mockMyBooths.map((booth) => (
            <li
              key={booth.id}
              className="flex items-center gap-6 cursor-pointer"
              onClick={() => handleBoothClick(booth.id)}
            >
              <Image
                src={booth.posterImageUrl}
                alt={booth.name}
                width={120}
                height={120}
                className="rounded-md object-cover"
              />
              <div className="text-base">
                <h4 className="text-lg font-bold">{booth.name}</h4>
                <p>• {booth.location}</p>
                <p>• {booth.date}</p>
              </div>
            </li>
          ))}

          {/* 신규 부스 등록 */}
          <li
            onClick={handleRegisterClick}
            className="flex items-center gap-6 cursor-pointer"
          >
            <div className="w-30 h-30 rounded-md bg-gray-200 flex items-center justify-center text-3xl text-gray-500">+</div>
            <span className="text-lg font-bold">신규 부스 등록하기</span>
          </li>
        </ul>
      </div>
    </div>
  )
}
