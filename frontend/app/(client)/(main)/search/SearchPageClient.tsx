'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { DayPicker } from 'react-day-picker';
import 'react-day-picker/dist/style.css';
import { fetchWithCredentials } from '@/libs/fetchWithCredentials';
import { FestivalCard } from '@/components/FestivalCard';
import Header from '@/components/Headers';

type Booth = {
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
};

type Festival = {
  id: number;
  posterImageUrl: string;
  mapImageUrl: string;
  name: string;
  startDate: string;
  endDate: string;
  location: string;
  description: string;
  likeCount: number;
  booths: Booth[];
};

interface UserData {
  id: number;
  name: string;
  email: string;
  role: 'USER' | 'ADMIN' | 'HOST'; // 또는 string도 가능
  university: string;
  major: string;
  profileImageUrl: string;
}

export default function SearchPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const query = searchParams.get('q') || '';
  const isSearching = !!query.trim();

  const [selectedDate, setSelectedDate] = useState<Date | undefined>();
  const [allFestivals, setAllFestivals] = useState<Festival[]>([]);
  const [festivals, setFestivals] = useState<Festival[]>([]);
  const [univList, setUnivList] = useState<string[]>([]);
  const [userData, setUserData] = useState<UserData | null>(null); // 사용자 데이터 상태

  const extractUniversityName = (festivalName: string): string | null => {
    const regex = /([\w가-힣]+?)(대학교|대)/;
    const match = festivalName.match(regex);
    if (!match) return null;
    return match[1] + '대학교';
  };

  useEffect(() => {
    const fetchFestivals = async () => {
      try {
        const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/festivals`);
        const data: Festival[] = await res.json();

        setAllFestivals(data);

        // 대학교별 가장 좋아요 많은 축제 top6 계산
        const univLikeMap = new Map<string, number>();
        data.forEach((festival) => {
          const univ = extractUniversityName(festival.name);
          if (univ) {
            const current = univLikeMap.get(univ) || 0;
            if (festival.likeCount > current) {
              univLikeMap.set(univ, festival.likeCount);
            }
          }
        });
        const top6Univs = Array.from(univLikeMap.entries())
          .sort((a, b) => b[1] - a[1])
          .slice(0, 6)
          .map(([univ]) => univ);
        setUnivList(top6Univs);
      } catch (error) {
        console.error('Failed to fetch festivals:', error);
      }
    };

    fetchFestivals();
  }, []);

  useEffect(() => {
    if (allFestivals.length === 0) return;

    let filtered = allFestivals;

    if (isSearching) {
      filtered = filtered.filter((festival) =>
        festival.name.includes(query)
      );
    }

    if (selectedDate) {
      const selected = selectedDate.toLocaleDateString('sv-SE'); // yyyy-mm-dd
      filtered = filtered.filter((festival) => {
        const start = festival.startDate.slice(0, 10);
        const end = festival.endDate.slice(0, 10);
        return selected >= start && selected <= end;
      });
    }

    setFestivals(filtered);
  }, [allFestivals, query, isSearching, selectedDate]);

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/users/me`);
        if (!res.ok) {
          console.error('Failed to fetch user data:', res.status);
          return;
        }
        const userData = await res.json();
        setUserData(userData);
        // 여기서 userData를 사용하여 필요한 작업 수행
      } catch (error) {
        console.error('Failed to fetch user data:', error);
      }
    };
    fetchUserData();
  }, []);

  return (
    <>
      <Header isBackButton={isSearching} title="Search" />
      <div className="flex flex-col justify-center relative p-4 pt-16 space-y-6">
        {/* 검색창 */}
        <div
          className="flex items-center rounded-xl px-4 py-2"
          style={{ border: '2px solid rgba(51, 85, 51, 0.7)' }}
        >
          <span className="text-gray-400 mr-2">🔍</span>
          <input
            type="text"
            placeholder="학교, 부스 이름 검색"
            className="w-full outline-none text-sm"
            defaultValue={query}
            onKeyDown={(e) => {
              if (e.key === 'Enter') {
                const input = (e.target as HTMLInputElement).value.trim();
                router.push(`/search?q=${encodeURIComponent(input)}`);
              }
            }}
          />
        </div>

        {/* 학교선택, 날짜선택 - 검색 없을 때만 */}
        {!isSearching && (
          <>
            {/* 학교 선택 */}
            <div className="space-y-2">
              <h2 className="font-semibold mb-2">학교 선택</h2>
              <div className="flex flex-wrap gap-2 justify-center">
                {univList.map((univ) => {
                  const matchedFestival = festivals.find(
                    (festival) =>
                      extractUniversityName(festival.name) === univ
                  );
                  if (!matchedFestival) return null;
                  return (
                    <Link href={`/festival/${matchedFestival.id}`} key={univ}>
                      <button
                        className="w-[100px] h-[37px] flex items-center justify-center border border-[#335533] border-opacity-50 text-black rounded-full text-sm hover:bg-[#335533] hover:text-white hover:border-opacity-100 active:bg-[#335533] active:text-white active:border-opacity-100 transition-all duration-200"
                      >
                        {univ}
                      </button>
                    </Link>
                  );
                })}
              </div>

              {/* 날짜 선택 */}
              <div className="flex justify-center mt-4">
                <div className="rounded-xl shadow-[0_4px_12px_rgba(0,0,0,0.15)] bg-white p-4 inline-block">
                  <style>{`
                    .rdp-root {
                      --rdp-accent-color: #335533;
                      --rdp-today-color: #335533;
                    }
                  `}</style>
                  <DayPicker
                    mode="single"
                    selected={selectedDate}
                    onSelect={setSelectedDate}
                  />
                </div>
              </div>
            </div>
          </>
        )}

        {/* 축제 카드 리스트 */}
        <div className="flex flex-col items-center gap-6">
          {festivals.length > 0 ? (
            festivals.map((festival) => (
              <FestivalCard key={festival.id} festival={festival} userId={userData!.id}/>
            ))
          ) : (
            <p className="text-center text-gray-500 mt-10 text-base">
              검색 결과가 없습니다.
            </p>
          )}
        </div>
      </div>
    </>
  );
}
