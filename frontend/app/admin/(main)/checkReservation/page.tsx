import { redirect } from "next/navigation";
import { fetchWithCredentials } from "@/libs/fetchwithCredentialsServer";

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
};

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
  booths: MyBoothdata[];
}[];

export default async function CheckReservationPage() {
  const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/users/me/booths`);

  const text = await res.text();
  if (!res.ok) {
    console.error("Failed to fetch:", res.status);
    return (
      <div className="flex items-center justify-center h-screen">
        <p className="text-red-500">
          부스 데이터를 불러오는데 실패했습니다. 잠시 후 다시 시도해주세요.
        </p>
      </div>
    );
  }

  const json = JSON.parse(text);
  const festivalsData: MyFestivalBoothData = json.festivals ?? [];
  const boothdata: MyBoothdata[] = festivalsData.flatMap((festival) => festival.booths ?? []);

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
