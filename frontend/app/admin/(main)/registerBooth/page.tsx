'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import Header from '@/components/Headers'
import { fetchWithCredentials } from '@/libs/fetchWithCredentials'

type Festival = {
  id: string
  name: string
}

import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
  } from "@/components/ui/select"

export default function RegisterBoothPage() {
  const router = useRouter()
  const [festivals, setFestivals] = useState<Festival[]>([])
  const [form, setForm] = useState({
    festivalId: '',
    name: '',
    host: '',
    location: '',
    description: '',
    startDateTime: '',
    endDateTime: '',
    posterImageUrl: '/src/booth1.png',
    eventImageUrl: '/src/booth1_event1.png',
  })

  useEffect(() => {
    const fetchFestivals = async () => {
      const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/festivals`)
      const data = await res.json()
      setFestivals(data)
    }
    fetchFestivals()
  }, [])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async () => {
    const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/festivals/${form.festivalId}/booths/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form),
    })

    if (res.ok) {
      alert('부스가 성공적으로 등록되었습니다.')
      router.push('/admin/manageBooth') // 혹은 부스 리스트로 이동
    } else {
      alert('등록 실패!')
    }
  }

  return (
    <>
        <Header isBackButton={true} title="신규 부스 등록" />
        <div className="p-4 mt-16 space-y-10">

        {/* 이미지 추가 */}
        <div className="w-full h-40 bg-gray-200 flex items-center justify-center text-gray-500 text-lg rounded">
            + 이미지 추가
        </div>

        {/* 축제 선택 */}
        <div>
            <label className="text-base font-semibold">축제 선택</label>
            <Select onValueChange={(value) => {console.log('선택한 페스티벌 id:', value); setForm({ ...form, festivalId: value })}}>
                <SelectTrigger className="w-full border-b py-2">
                    <SelectValue placeholder="축제를 선택하세요" />
                </SelectTrigger>
                <SelectContent>
                    {festivals.map(festival => (
                        <SelectItem key={festival.id} value={festival.id}>
                            {festival.name}
                        </SelectItem>
                    ))}
                </SelectContent>
            </Select>
        </div>

        <input name="host" value={form.host} onChange={handleChange} placeholder="단체명" className="w-full border-b py-2" />
        <input name="name" value={form.name} onChange={handleChange} placeholder="부스 이름" className="w-full border-b py-2" />
        <input name="location" value={form.location} onChange={handleChange} placeholder="부스 위치" className="w-full border-b py-2" />

        {/* 날짜 및 시간 */}
        <div className="flex gap-2">
            <input
            name="startDateTime"
            value={form.startDateTime}
            onChange={handleChange}
            type="datetime-local"
            className="w-1/2 border-b py-2"
            />
            <input
            name="endDateTime"
            value={form.endDateTime}
            onChange={handleChange}
            type="datetime-local"
            className="w-1/2 border-b py-2"
            />
        </div>

        {/* 인원 수 & 상세 정보 */}
        <input
            name="description"
            value={form.description}
            onChange={handleChange}
            placeholder="상세 정보 (판매 메뉴, 공지사항 등)"
            className="w-full border-b py-2"
        />

        {/* 이미지 URL */}
        <p className="text-base font-semibold">이벤트 이미지 등록</p>
        <div className="w-full h-40 bg-gray-200 flex items-center justify-center text-gray-500 text-lg rounded">
            + 이미지 추가
        </div>


        <button
            onClick={handleSubmit}
            className="w-full bg-[#335533] text-white py-2 rounded-lg font-semibold"
        >
            저장
        </button>
        </div>
    </>
  )
}
