import { IoHeart } from 'react-icons/io5';
import Image from 'next/image';
import Header from '@/components/Headers';
import LikeButton from '@/components/LikeButton';
import { formatDate } from '@/libs/utils';
import Link from 'next/link';
import { fetchWithCredentials } from '@/libs/fetchWithCredentials';

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

export default async function BoothDetailPage({ params }:{ params: Promise<{ festivalId: string; boothId: string }> }) {
  const { festivalId, boothId } = await params;
  const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/festivals/${festivalId}`);
  const festival : Festivaltype = await res.json();
  const booth = festival.booths.find((booths) => booths.id === Number(boothId));
  // const [liked, setLiked] = useState(false);
  // const [likeCount, setLikeCount] = useState(booth.likeCount);

  // const toggleLike = () => {
  //   setLiked(prev => !prev);
  //   setLikeCount(prev => prev + (liked ? -1 : 1));
  // };

  if (!booth) {
    return <div>부스를 찾을 수 없습니다.</div>;
  }

  return (
    <>
      <Header isBackButton={true} title={booth.name} />
      <div className="relative p-4 pt-16 space-y-6">

        {/* 포스터 이미지 + 하트 버튼 */}
        <div className="relative w-full h-[270px]">
          <Image
            src={booth.posterImageUrl}
            alt="부스 포스터"
            fill
            className="object-cover"
          />
          <LikeButton
            initialLiked={false}
            size={25}
            className="absolute top-2 right-2"
          />
        </div>

        {/* 부스 기본 정보 */}
        <div>
          <div className="flex items-center gap-2 mt-4">
            <h2 className="text-xl font-bold">{booth.name}</h2>
            <div className="flex items-center gap-1 text-[15px] text-black/60">
              <IoHeart size={18} className="text-red-500" />
              {booth.likeCount}
            </div>
          </div>
          <ul className="list-disc pl-5 text-sm space-y-1 mt-2">
            <li><strong>기간</strong> : {formatDate(booth.startDateTime)}-{formatDate(booth.endDateTime)}</li>
            <li><strong>위치</strong> : {booth.location}</li>
          </ul>
        </div>

        {/* 부스 상세 정보 */}
        <div className="pt-4 border-t border-[#335533b3] space-y-2">
          <h1 className="text-xl font-bold">상세 정보</h1>
          <ul className="list-disc pl-5 text-sm space-y-1">
            {booth.description.split('<br />').map((item, idx) => (
              <li key={idx}>{item.trim()}</li>
            ))}
          </ul>
        </div>


        {/* 이벤트 배너 이미지 */}
        <Image
          src={booth.eventImageUrl}
          alt="이벤트"
          width={500}
          height={300}
          className="w-full object-cover rounded-lg"
        />

        {/* 예약 버튼 */}
        <div className="fixed bottom-0 left-0 w-full py-4 bg-white border-t border-gray-200 z-50 flex justify-center">
          <Link href={`/festival/${festivalId}/booth/${boothId}/reservation`}>
            <button className="w-[289px] h-[48px] bg-[#335533] text-white font-bold text-[20px] rounded-lg">
              예약하기
            </button>
          </Link>
        </div>

      </div>
    </>
  );
}