// app/admin/(main)/[festivalId]/page.tsx

import Header from "@/components/Headers";
import Link from "next/link";
import { formatDate } from "@/libs/utils";
import Image from "next/image";

// type Props = {
//     params: {
//         festivalId: string;
//     };
// };


type Festivaltype = {
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
}

export default async function FestivalDetailPage({ params }: { params: Promise<{ festivalId: string }> }) {
  const { festivalId } = await params;
  const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/festivals/${festivalId}`);
  const festival : Festivaltype = await res.json();


  return (
      <>
          <Header isBackButton={true} title={festival.name}/>
          <div className="relative p-4 pt-16 space-y-6">
              {/* 뒤로가기 & 제목 */}
              

              {/* 포스터 */}
              {/* <img
                  src={festival.posterImageUrl}
                  alt="축제 포스터"
                  className="w-full rounded-lg shadow"
              /> */}
              <Image
                src={festival.posterImageUrl}
                alt="축제 포스터"
                width={312}
                height={312}
                className="rounded-lg shadow"
              />

              {/* 간단 정보 */}
              <div>
                  <h2 className="text-xl font-semibold mt-4">{festival.description}</h2>
                  <p className="mt-1">📅 {formatDate(festival.startDate)} ~ {formatDate(festival.endDate)}</p>
                  <p>📍 {festival.location}</p>
              </div>

              {/* 부스 목록 */}
              <div>
                  <h3 className="font-semibold mb-2">부스</h3>
                  <div className="flex overflow-x-auto space-x-3 scrollbar-hide pb-2 whitespace-nowrap">
                  {festival.booths.map(booth=> (

                      // 부스 클릭하면 부스 페이지로 넘어가도록 링크 추가
                      <Link
                      key={booth.id}
                      href={`/admin/festival/${festival.id}/booth/${booth.id}`}
                      className="flex-shrink-0"
                    >
                      <Image
                        src={booth.posterImageUrl}
                        alt={`부스 ${booth.id}`}
                        width={128}
                        height={128}
                        className="object-cover rounded cursor-pointer"
                      />
                    </Link>
                      // <img
                      // key={booth.boothId}
                      // src={booth.posterImageUrl}
                      // alt={`부스 ${booth.boothId}`}
                      // className="w-32 h-32 object-cover rounded"
                      // />
                  ))}
                  </div>
              </div>

              {/* 지도 */}
              <div>
                  <h3 className="font-semibold mb-2">지도</h3>
                  <Image
                    src={festival.mapImageUrl}
                    alt="지도 이미지"
                    width={312}
                    height={312}
                    className="w-full rounded-lg shadow"
                  />
              </div>
          </div>
      </>
  );
}
