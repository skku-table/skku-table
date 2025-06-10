import { cookies } from "next/headers";
import ManageBoothCard from '@/components/ManageBoothCard';


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


export default async function AdminBoothManagePage() {
  const cookieHeader = cookies().toString();

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
  return (
    <ManageBoothCard boothsdata={boothsdata}/>
  )
}
