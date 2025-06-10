import { IoHeart } from 'react-icons/io5';
import Header from "@/components/Headers"
import Image from "next/image"
import { cookies } from "next/headers";
import { formatDate } from '@/libs/utils';
import { formatTime } from '@/libs/utils';
import { fetchWithCredentials } from '@/libs/fetchWithCredentials';
import AdminSelectBooth from '@/components/AdminSelectBooth';

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
  type MyFestivalBoothData = {
    id: number;
    posterImageUrl: string;
    mapImageUrl: string;
    name: string;
    startDate: string;
    endDate: string;
    location: string;
    description: string;
    likeCount: number;
    booths: MyBoothdata[]
    }[];
  type BoothforReservation = {
    booth: {
      id: number;
      name: string;
      location: string;
      startTime: string;
      endTime: string;
      posterImageUrl: string;
      likeCount: number;
    }
    reservations: {
      reservationId: number;
      userId: number;
      userName: string;
      reservationTime: string;
      numberOfPeople: number;
      paymentMethod: string;
      createdAt: string;
    }[];
  };
  

export default async function CheckReservationDetail({params}: {params: {festivalId: string, boothId: string}}) {
    const cookieHeader = await cookies().toString();

    //try fetchwithcredential
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/users/me/booths`, {
      headers: {
        Cookie: cookieHeader,
      },
      cache: 'no-store', // 서버 컴포넌트에서 fetch는 기본적으로 SSG라 no-store 권장
    });
    if (!res.ok) {
      console.error('Failed to fetch:', res.status)
      return null
    }
    const json = await res.json();
    const festivalsData: MyFestivalBoothData = json.festivals ?? [];
    const boothsdata: MyBoothdata[] = festivalsData.flatMap(festival => festival.booths ?? []);

    const festivalId = await Number(params.festivalId);
    const boothId = await Number(params.boothId);
    const boothres=await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/reservations/festival/${festivalId}/booth/${boothId}`, {
      headers: {
        Cookie: cookieHeader,
      },

      cache: 'no-store', // 서버 컴포넌트에서 fetch는 기본적으로 SSG라 no-store 권장
    });
    

    const booth: BoothforReservation = boothres.ok ? await boothres.json() : []

    return (
      <div>
        <Header isBackButton={false} title="Check Reservation" />
        <div className="relative p-4 pt-16 space-y-6">
          <AdminSelectBooth boothsdata={boothsdata} boothname={booth.booth.name}/>
          <div className="flex justify-center mb-4">
            <Image
                src={booth.booth.posterImageUrl}
                alt="부스 포스터"
                width={312}
                height={312}
                className="rounded-lg shadow-lg"
              />
          </div>
            <div>
              <div className="flex items-center gap-2 mt-4 ml-2">
                <h2 className="text-xl font-bold">{booth.booth.name}</h2>
                <div className="flex items-center gap-1 text-[15px] text-black/60">
                  <IoHeart size={18} className="text-red-500" />
                  {booth.booth.likeCount}
                </div>
              </div>
              <ul className="list-disc pl-5 text-sm space-y-1 mt-2">
                <li><strong>기간</strong> : {formatDate(booth.booth.startTime)} ~ {formatDate(booth.booth.endTime)}</li>
                <li><strong>위치</strong> : {booth.booth.location}</li>
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
                          <p className='text-lg font-bold mb-2'>{reservation.userName}</p>
                          <ul>
                            <li><strong>예약일</strong> : {formatDate(reservation.reservationTime)}</li>
                            <li><strong>예약시간</strong> : {formatTime(reservation.reservationTime)}</li>
                            <li><strong>예약인원</strong> : {reservation.numberOfPeople}</li>
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