'use client'

import Image from 'next/image';
import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { fetchWithCredentials } from '@/libs/fetchWithCredentials';
import LikeFestivalButton from '@/components/LikeFestivalButton';
import LikeBoothButton from '@/components/LikeBoothButton';


interface User {
  id: number;
  name: string;
  email: string;
  role: string;
  university: string | null;
  major: string | null;
  profileImageUrl: string | null;
}

interface Festival {
  id: number;
  posterImageUrl: string;
  name: string;
  startDate: string;
  endDate: string;
  likeCount: number;
}

interface Booth {
  id: number;
  name: string;
  host: string;
  location: string;
  description: string;
  startDateTime: string;
  endDateTime: string;
  likeCount: number;
  posterImageUrl: string | null;
}


export default function MyPage() {
  const [tab, setTab] = useState<'univ' | 'booth'>('univ');
  const [user, setUser] = useState<User | null>(null);
  const [likedFestivals, setLikedFestivals] = useState<Festival[]>([]);
  const [likedBooths, setLikedBooths] = useState<Booth[]>([]);
  const router = useRouter();

  useEffect(() => {
    const fetchAll = async () => {
      try {
        const userRes = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/users/me`);
        const userData = await userRes.json();
        setUser(userData);

        const [festivalRes, boothRes] = await Promise.all([
          fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/users/${userData.id}/likes/festivals`),
          fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/users/${userData.id}/likes/booths`)
        ]);

        if (festivalRes.ok) {
          const festivalData = await festivalRes.json();
          setLikedFestivals(festivalData);
        }

        if (boothRes.ok) {
          const boothData = await boothRes.json();
          setLikedBooths(boothData);
        }

      } catch (error) {
        console.error('정보 불러오기 실패:', error);
      }
    };

    fetchAll();
  }, []);

  if (!user) return <div className="p-4">로딩 중...</div>;

  return (
    <div className="px-4 py-6 font-sans">
      <h1 className="text-xl font-bold mb-4">My Page</h1>

      {/* 사용자 정보 */}
      <div className="flex items-center mb-6">
        <Image
          src={user.profileImageUrl ?? '/src/users/basic_profile.png'}
          alt="프로필 사진"
          width={60}
          height={60}
          className="rounded-full border border-gray-300"
        />
        <div className="ml-4">
          <div className="font-semibold">{user.name}</div>
          <div className="text-sm text-black">
            {user.university ?? '소속 학교 미입력'} {user.major ? ` ${user.major}` : ''}
          </div>
        </div>
      </div>

      {/* 나의 정보 */}
      <div className="mb-6">
        <h2 className="font-semibold border-b-2 border-[#475E51] pb-1 mb-2">나의 정보</h2>
        <div className="text-sm">
          <div
            className="border-b py-3 cursor-pointer"
            onClick={() => router.push('/myprofile/edit')}
          >
            프로필 수정
          </div>
          <div className="border-b py-3">로그아웃</div>
        </div>
      </div>

      {/* 나의 저장 */}
      <div>
        <h2 className="font-semibold border-b-2 border-[#475E51] pb-1 mb-2">나의 저장</h2>

        {/* 탭 버튼 */}
        <div className="flex w-full justify-start gap-4 mb-4">
          <button
            onClick={() => setTab('univ')}
            className={`w-[110px] h-[35px] rounded-[20px] text-base font-bold transition
              ${tab === 'univ' ? 'bg-[#335533] text-white' : 'bg-[#335533]/10 text-black'}`}
          >
            대학교
          </button>
          <button
            onClick={() => setTab('booth')}
            className={`w-[110px] h-[35px] rounded-[20px] text-base font-bold transition
              ${tab === 'booth' ? 'bg-[#335533] text-white' : 'bg-[#335533]/10 text-black'}`}
          >
            부스
          </button>
        </div>
        {/* 좋아요한 축제 / 부스 */}
        <div className="grid grid-cols-2 gap-3">
        {tab === 'univ' && likedFestivals.map(festival => (
          <div key={festival.id} className="rounded-lg overflow-hidden border border-gray-200 w-[150px] h-[150px]">
            <div className="relative w-full h-full">
              <Image
                src={festival.posterImageUrl}
                alt={festival.name}
                fill
                className="object-cover"
              />
              <LikeFestivalButton festivalId={festival.id} />
            </div>
          </div>
        ))}

        {tab === 'booth' && likedBooths.map(booth => (
          <div key={booth.id} className="rounded-lg overflow-hidden border border-gray-200 w-[150px] h-[150px]">
            <div className="relative w-full h-full">
              {booth.posterImageUrl ? (
                <Image
                  src={booth.posterImageUrl}
                  alt={booth.name}
                  fill
                  className="object-cover"
                />
              ) : (
                <div className="w-full h-full bg-gray-200 flex items-center justify-center text-[12px] text-gray-500">
                  이미지 없음
                </div>
              )}
              <LikeBoothButton boothId={booth.id} />
            </div>
          </div>
        ))}
      </div>
      </div>
    </div>
  );
}
