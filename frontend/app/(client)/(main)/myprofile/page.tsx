'use client';

import { useEffect, useState } from 'react';

interface User {
  id: number;
  name: string;
  email: string;
  role: string;
}

export default function MyPage() {
  const [tab, setTab] = useState<'univ' | 'booth'>('univ');
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    async function fetchUser() {
      try {
        const res = await fetch('http://localhost:8080/users/me', {
          credentials: 'include', // 쿠키 인증이 필요한 경우
        });
        if (!res.ok) throw new Error('사용자 정보를 불러오지 못했습니다.');
        const data = await res.json();
        setUser(data);
      } catch (err) {
        console.error(err);
      }
    }

    fetchUser();
  }, []);

  const dummyData = [
    { id: 1, title: '맹자萬綠', imageUrl: '/sample1.png' },
    { id: 2, title: '천준야', imageUrl: '/sample2.png' },
  ];

  return (
    <div className="p-4 space-y-8">
      {/* 헤더 */}
      <h1 className="text-xl font-bold">My Page</h1>

      {/* 프로필 카드 */}
      <div className="flex items-center space-x-4">
        <img
          src="/user-profile.jpg"
          alt="profile"
          className="w-16 h-16 rounded-full object-cover"
        />
        <div className="flex-1">
          <p className="font-semibold">{user ? user.name : '로딩 중...'}</p>
          <p className="text-sm text-gray-600">성균관대학교 소프트웨어학과</p>
          <div className="mt-2 space-x-2">
            <button className="px-4 py-1 bg-green-800 text-white rounded">프로필 수정</button>
            <button className="px-4 py-1 border border-green-800 text-green-800 rounded">학교 인증</button>
          </div>
        </div>
      </div>

      {/* 나의 정보 */}
      <div>
        <h2 className="text-lg font-semibold border-b border-green-900 mb-2">나의 정보</h2>
        <div className="divide-y">
          <div className="py-2">기본 정보</div>
          <div className="py-2">언어 설정</div>
        </div>
      </div>

      {/* 나의 저장 */}
      <div>
        <h2 className="text-lg font-semibold border-b border-green-900 mb-2">나의 저장</h2>
        <div className="flex space-x-2 mb-4">
          <button
            className={`px-4 py-1 rounded-full ${tab === 'univ' ? 'bg-green-900 text-white' : 'bg-gray-100 text-black'}`}
            onClick={() => setTab('univ')}
          >
            대학교
          </button>
          <button
            className={`px-4 py-1 rounded-full ${tab === 'booth' ? 'bg-green-900 text-white' : 'bg-gray-100 text-black'}`}
            onClick={() => setTab('booth')}
          >
            부스
          </button>
        </div>

        <div className="grid grid-cols-2 gap-4">
          {dummyData.map((item) => (
            <div key={item.id} className="relative">
              <img
                src={item.imageUrl}
                alt={item.title}
                className="rounded-md object-cover w-full aspect-square"
              />
              <div className="absolute top-2 right-2 text-red-500 text-xl">❤️</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
