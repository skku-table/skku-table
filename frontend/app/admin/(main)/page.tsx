import Image from "next/image";
import LikeButton from "@/components/LikeButton";
import { IoHeartSharp } from "react-icons/io5";
import Link from "next/link";
import Header from "@/components/Headers"
import { formatDate } from "@/libs/utils";



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
  const res= await fetch(`${process.env.NEXT_PUBLIC_API_URL}/festivals`);
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
            <div key={festival.id}>
                <div className="relative w-[290px] h-[290px] mx-auto">
                  <Link href={`/admin/festival/${festival.id}`}>
                    <Image
                      src={festival.posterImageUrl}
                      alt="festival poster"
                      fill
                      className="rounded-xl object-cover cursor-pointer"
                    />
                  </Link>
                  <LikeButton
                    initialLiked={false}
                    size={25}
                  />
                </div>

              <div className="mt-2">
                <p className="text-lg font-bold">{festival.name}</p>
                <p className="text-lg">
                  {formatDate(festival.startDate)} ~ {formatDate(festival.endDate)}
                </p>
                {/* <p className="flex items-center gap-1" style={{ fontSize: "15px", color: "rgba(0, 0, 0, 0.6)" }}>
                  <IoHeartSharp style={{ color: "red" }} />
                  40
                </p> */}
                <p className="flex items-center gap-1 text-[15px] text-black/60">
                  <IoHeartSharp style={{ color: "red" }} />
                  {festival.likeCount}
                </p>
              </div>
            </div>
          ))}
        </div>

      </main>
    </>
  );
}
