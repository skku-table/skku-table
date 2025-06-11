'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Image from 'next/image';
import { fetchWithCredentials } from '@/libs/fetchWithCredentials';
import Header from '@/components/Headers';

export default function EditProfilePage() {
  const router = useRouter();

  const [name, setName] = useState('');
  const [university, setUniversity] = useState('');
  const [major, setMajor] = useState('');
  const [profileImage, setProfileImage] = useState<File | null>(null);
  const [previewImageUrl, setPreviewImageUrl] = useState<string | null>(null);
  const [imageDeleted, setImageDeleted] = useState(false);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/users/me`);
        const data = await res.json();

        setName(data.name || '');
        setUniversity(data.university || '');
        setMajor(data.major || '');
        if (data.profileImageUrl) setPreviewImageUrl(data.profileImageUrl);
        else setPreviewImageUrl('/src/userprofile.png');
      } catch (err) {
        console.error('유저 정보 불러오기 실패:', err);
      }
    };
    fetchUser();
  }, []);

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setProfileImage(file);
      setPreviewImageUrl(URL.createObjectURL(file));
      setImageDeleted(false);
    }
  };

  const handleImageUpload = async () => {
    if (!profileImage) return;

    const formData = new FormData();
    formData.append('image', profileImage);

    try {
      const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/users/me/profile-image`, {
        method: 'PUT',
        body: formData,
      });

      if (!res.ok) throw new Error('이미지 업로드 실패');

      const url = await res.text(); // ← 여기 수정
      setPreviewImageUrl(url);
    } catch (err) {
      console.error(err);
      alert('이미지 업로드 중 오류가 발생했습니다.');
    }
  };

  const handleImageDelete = () => {
    setProfileImage(null);
    setPreviewImageUrl('/src/userprofile.png');
    setImageDeleted(true);
  };

  const handleSave = async () => {
    try {
      if (imageDeleted) {
        const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/users/me/profile-image`, {
          method: 'DELETE',
        });

        if (!res.ok) throw new Error('이미지 삭제 실패');
      } else if (profileImage) {
        await handleImageUpload();
      }

      const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/users/me`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name,
          university,
          major,
        }),
      });

      if (!res.ok) throw new Error('저장 실패');

      alert('저장되었습니다.');
      router.back();
    } catch (err) {
      console.error(err);
      alert('저장 중 오류가 발생했습니다.');
    }
  };

  return (
    <>
      <Header isBackButton={true} title="프로필 수정" />

      <div className="flex flex-col px-4 pb-32 pt-16">
        <div className="flex flex-col items-center">
          <div className="w-28 h-28 rounded-full overflow-hidden">
            <Image
              src={previewImageUrl || '/src/userprofile.png'}
              alt="Profile"
              width={100}
              height={100}
              className="object-cover w-full h-full"
            />
          </div>

          <div className="flex gap-4 mt-3">
            <label
              className="w-[130px] h-[30px] border border-[#335533] rounded-md flex items-center justify-center cursor-pointer font-bold text-sm text-[#335533] bg-white"
              htmlFor="profileImage"
            >
              사진 선택
            </label>
            <input
              id="profileImage"
              name="profileImage"
              type="file"
              accept="image/*"
              className="hidden"
              onChange={handleImageChange}
            />
            <button
              onClick={handleImageDelete}
              className="w-[130px] h-[30px] border border-[#335533] rounded-md font-bold text-sm text-[#335533] bg-white"
            >
              사진 삭제
            </button>
          </div>
        </div>

        <form className="flex flex-col gap-6 mt-8">
          <div>
            <label className="text-sm font-bold block mb-1">이름</label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full border rounded-md px-3 py-2 outline-green-900"
              placeholder="이름 입력"
            />
          </div>

          <div>
            <label className="text-sm font-bold block mb-1">대학교</label>
            <input
              type="text"
              value={university}
              onChange={(e) => setUniversity(e.target.value)}
              className="w-full border rounded-md px-3 py-2 outline-green-900"
              placeholder="학교 이름 입력"
            />
          </div>

          <div>
            <label className="text-sm font-bold block mb-1">학과명</label>
            <input
              type="text"
              value={major}
              onChange={(e) => setMajor(e.target.value)}
              className="w-full border rounded-md px-3 py-2 outline-green-900"
              placeholder="학과명 입력"
            />
          </div>
        </form>
      </div>

      <div className="fixed bottom-0 left-0 w-full py-4 bg-white border-t border-gray-200 z-50 flex justify-center">
        <button
          onClick={handleSave}
          className="w-[289px] h-[48px] bg-[#335533] text-white font-bold text-[20px] rounded-lg"
        >
          저장
        </button>
      </div>
    </>
  );
}
