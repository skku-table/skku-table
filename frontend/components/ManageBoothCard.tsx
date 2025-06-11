'use client'

import Image from 'next/image'
import { useRouter } from 'next/navigation'
import Header from '@/components/Headers'
import { formatDate } from '@/libs/utils';


type MyBoothdata = {
    id: number;
    festivalId: number;
    festivalName: string;
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

export default function ManageBoothCard({boothsdata}:{boothsdata: MyBoothdata[]}) {
    const router = useRouter()

    const handleBoothClick = (festivalId: number, boothId: number) => {
    router.push(`/admin/checkReservation/${festivalId}/${boothId}`)
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
            {boothsdata.map((booth) => (
            <li
                key={booth.id}
                className="flex items-center gap-6 cursor-pointer rounded-md"
                onClick={() => handleBoothClick(booth.festivalId, booth.id)}
            >
                <Image
                src={booth.posterImageUrl}
                alt={booth.name}
                width={120}
                height={120}
                className="rounded-md object-cover hover:ring-blue-500"
                />
                <div className="text-base">
                <h4 className="text-lg font-bold">{booth.name}</h4>
                <p>• {booth.location}</p>
                <p>• {formatDate(booth.startDateTime)}-{formatDate(booth.endDateTime)}</p>
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
    
