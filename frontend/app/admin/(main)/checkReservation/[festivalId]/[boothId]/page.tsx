import { IoHeart } from 'react-icons/io5';
import Header from "@/components/Headers";
import Image from "next/image";
import { cookies } from "next/headers";
import { formatDate } from '@/libs/utils';
import AdminSelectBooth from '@/components/AdminSelectBooth';
import CheckReservationCard from '@/components/CheckReservationCard';


// 타입 정의

// 부스 정보 타입
interface MyBoothdata {
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

// 축제 정보 + 부스 리스트 타입
interface MyFestivalBoothData extends Array<{
  id: number;
  posterImageUrl: string;
  mapImageUrl: string;
  name: string;
  startDate: string;
  endDate: string;
  location: string;
  description: string;
  likeCount: number;
  booths: MyBoothdata[];
}> {}

// 예약 정보 타입
interface ReservationType {
  reservationId: number;
  userId: number;
  userName: string;
  reservationTime: string;
  numberOfPeople: number;
  paymentMethod: string;
  createdAt: string;
}

export default async function CheckReservationDetail({ params }: { params: Promise<{ festivalId: string; boothId: string }> }) {
  const cookieHeader = cookies().toString();

  const { festivalId, boothId } = await params;

  // 유저가 만든 부스들 조회
  const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/users/me/booths`, {
    headers: {
      Cookie: cookieHeader,
    },
    cache: 'no-store',
  });

  if (!res.ok) {
    console.error('Failed to fetch:', res.status);
    return null;
  }

  const json = await res.json();
  const festivalsData: MyFestivalBoothData = json.festivals ?? [];
  const boothsdata: MyBoothdata[] = festivalsData.flatMap(festival => festival.booths ?? []);
  const targetBooth = boothsdata.find(booth => booth.id === Number(boothId));
  if (!targetBooth) return <div>부스를 찾을 수 없습니다.</div>;

  // boothId 기반으로 타임슬롯 조회
  const slotRes = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/booths/${boothId}/timeslots`, {
    headers: {
      Cookie: cookieHeader,
    },
    cache: 'no-store',
  });
  if (!slotRes.ok) {
    console.error('타임슬롯 조회 실패:', slotRes.status);
    return <div>타임슬롯 데이터를 불러오는 데 실패했습니다.</div>;
  }
  const timeSlots = await slotRes.json();

  // 모든 타임슬롯에 대해 예약 정보 병렬 요청
  const reservations = await Promise.all(
    timeSlots.map(async (slot: any) => {
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/v2/reservations/timeslots/${slot.id}`, {
        headers: {
          Cookie: cookieHeader,
        },
        cache: 'no-store',
      });
      if (!res.ok) return [];
      const data = await res.json();
      return data.map((r: any) => ({
        reservationId: r.reservationId,
        userId: r.userId,
        userName: r.userName,
        reservationTime: r.timeSlotStartTime,
        numberOfPeople: r.numberOfPeople,
        paymentMethod: r.paymentMethod,
        createdAt: r.createdAt,
      }));
    })
  );
  const flatReservations: ReservationType[] = reservations.flat();

  return (
    <div>
      <Header isBackButton={false} title="Check Reservation" />
      <div className="relative p-4 pt-16 space-y-6">
        <AdminSelectBooth boothsdata={boothsdata} boothname={targetBooth.name} />
        <div className="flex justify-center mb-4">
          <Image
            src={targetBooth.posterImageUrl}
            alt="부스 포스터"
            width={312}
            height={312}
            className="rounded-lg shadow-lg"
          />
        </div>
        <div>
          <div className="flex items-center gap-2 mt-4 ml-2">
            <h2 className="text-xl font-bold">{targetBooth.name}</h2>
            <div className="flex items-center gap-1 text-[15px] text-black/60">
              <IoHeart size={18} className="text-red-500" />
              {targetBooth.likeCount}
            </div>
          </div>
          <ul className="list-disc pl-5 text-sm space-y-1 mt-2">
            <li><strong>기간</strong> : {formatDate(targetBooth.startDateTime)} ~ {formatDate(targetBooth.endDateTime)}</li>
            <li><strong>위치</strong> : {targetBooth.location}</li>
          </ul>
        </div>
        <div className="pt-4 border-t border-[#335533b3] space-y-2">
          <h1 className="text-xl font-bold mb-5">예약 현황</h1>
          <CheckReservationCard reservation={flatReservations} />
        </div>
      </div>
    </div>
  );
}