import { cookies } from "next/headers";
import { redirect } from "next/navigation"


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