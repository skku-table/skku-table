import Header from "@/components/Headers"
import { fetchWithCredentials } from "@/libs/fetchWithCredentials";
import { FestivalCard } from "@/components/FestivalCard";



type FestivalsData = {
  id: number;
  posterImageUrl: string;
  mapImageUrl: string;
  name: string;
  startDate: string;
  endDate: string;
  location: string;
  description: string;
  likeCount: number;
  booths:{
    id: number;
    name: string;
    host: string;
    location: string;
    description: string;
    startDateTime: string;
    endDateTime: string;
    likeCount: number;
    posterImageUrl: string;
    eventImageUrl: string;
    createdAt: string;
    updatedAt: string;
  }[]

  }[];


export default async function Page() {
  // 상태로 변경
  const res= await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/festivals`);
  const festivalsData: FestivalsData = await res.json();
    

  // 좋아요 토글 함수
  // const toggleLike = (festivalId: number) => {
  //   setFestivals(prevFestivals =>
  //     prevFestivals.map(festival =>
  //       festival.id === festivalId
  //         ? {
  //             ...festival,
  //             liked: !festival.liked,
  //             likeCount: festival.liked
  //               ? festival.likeCount - 1
  //               : festival.likeCount + 1,
  //           }
  //         : festival
  //     )
  //   );
  // };

  return (
    <>
      <Header isBackButton={false} title="Current Festivals"/>
      <main className="pt-[72px] p-4">
        {/* <h1 className="text-xl font-extrabold mb-4 sticky top-0 bg-white z-10 py-3">진행 중인 축제</h1> */}

        <div className="flex flex-col items-center gap-6">
          {festivalsData.map((festival) => (
            <FestivalCard key={festival.id} festival={festival} />
          ))}
        </div>

      </main>
    </>
  );
}
