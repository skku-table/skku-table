import { fetchWithCredentials } from "@/libs/fetchWithCredentials";
import { cookies } from "next/headers";
import { redirect } from "next/navigation"

// type Reservation = {
//   username: string
//   date: string // 예: '25.05.16'
//   time: string // 예: '오후 6:00'
//   people: number
// }

// type Booth = {
//   id: number
//   name: string
//   location: string
//   period: string
//   imageUrl: string
//   likeCount: number
//   reservations: Reservation[]
// }

// type AdminBoothList = Booth[]

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

// const mockAdminBooths: AdminBoothList = [
//   {
//     id: 1,
//     name: '다같이 추억 숲으로',
//     location: '삼성학술정보관 앞 잔디밭 3번 부스',
//     period: '5.16 - 5.17',
//     imageUrl: '/images/poster1.png',
//     likeCount: 24,
//     reservations: [
//       {
//         username: '심오비',
//         date: '25.05.16',
//         time: '오후 6:00',
//         people: 4,
//       },
//       {
//         username: '타메르',
//         date: '25.05.17',
//         time: '오후 7:30',
//         people: 2,
//       },
//     ],
//   },
//   {
//     id: 2,
//     name: 'Hotel AKDONG',
//     location: '율전 캠퍼스 중앙광장',
//     period: '5.16 - 5.17',
//     imageUrl: '/images/poster2.png',
//     likeCount: 12,
//     reservations: [
//       {
//         username: '유세윤',
//         date: '25.05.17',
//         time: '오후 5:00',
//         people: 3,
//       },
//     ],
//   },
//   {
//     id: 3,
//     name: '술이술이 마술이',
//     location: '제2과학관 옆 잔디밭',
//     period: '5.16 - 5.17',
//     imageUrl: '/images/poster3.png',
//     likeCount: 30,
//     reservations: [
//       {
//         username: '이장군',
//         date: '25.05.16',
//         time: '오후 6:30',
//         people: 5,
//       },
//     ],
//   },
// ]


export default async function CheckReservationPage() {
    const cookieHeader = cookies().toString();

    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/users/me/booths`, {
      headers: {
        Cookie: cookieHeader,
      },
      cache: 'no-store', // 서버 컴포넌트에서 fetch는 기본적으로 SSG라 no-store 권장
    });
    const text = await res.text();
    if (!res.ok) {
      console.error('Failed to fetch:', res.status)
      return null
    }
    const json = JSON.parse(text);
    const festivalsData: MyFestivalBoothData = json.festivals ?? [];
    const boothdata: MyBoothdata[] = festivalsData.flatMap(festival => festival.booths ?? []);

    if (boothdata.length !== 0) {
      const firstFestivalId = boothdata[0].festivalId;
      const firstBoothId = boothdata[0].id;
      redirect(`/admin/checkReservation/${firstFestivalId}/${firstBoothId}`);
    } else {
      return (
        <div className="flex items-center justify-center h-screen">
          <h1 className="text-2xl font-bold">등록된 부스가 없습니다.</h1>
        </div>
      );
    }
  }