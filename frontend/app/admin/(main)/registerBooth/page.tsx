'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import Header from '@/components/Headers'
import { fetchWithCredentials } from '@/libs/fetchWithCredentials'
import Image from 'next/image'

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
  const [previewEventUrl, setPreviewEventUrl] = useState<string | null>(null)
  const [previewBoothUrl, setPreviewBoothUrl] = useState<string | null>(null)
  const [form, setForm] = useState({
    festivalId: '',
    name: '',
    host: '',
    location: '',
    description: '',
    startDateTime: '',
    endDateTime: '',
    posterImage: null as File | null,
    eventImage: null as File | null,
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

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, files } = e.target
    if (files && files[0]) {
      const file = files[0]
      setForm(prev => ({
        ...prev,
        [name]: file,
      }))
      if (name === 'eventImage') {
        const url = URL.createObjectURL(file)
        setPreviewEventUrl(url)
      }
      if (name === 'posterImage') {
        const url = URL.createObjectURL(file)
        setPreviewBoothUrl(url)
      }
    }
  }
  

  const handleSubmit = async () => {
    const formData = new FormData()
    formData.append('name', form.name)
    formData.append('host', form.host)
    formData.append('location', form.location)
    formData.append('description', form.description)
    formData.append('startDateTime', form.startDateTime)
    formData.append('endDateTime', form.endDateTime)
    if (form.posterImage) formData.append('posterImage', form.posterImage)
    if (form.eventImage) formData.append('eventImage', form.eventImage)
  
    const res = await fetchWithCredentials(
      `${process.env.NEXT_PUBLIC_API_URL}/festivals/${form.festivalId}/booths/register`,
      {
        method: 'POST',
        body: formData,
      }
    )
  
    if (res.ok) {
      alert('부스 등록 완료!')
      router.push('/admin/manageBooth')
    } else {
      alert('부스 등록 실패')
    }
  }
  

  return (
    <>
        <Header isBackButton={true} title="신규 부스 등록" />
        <div className="p-4 mt-16 space-y-10">

        {/* 이미지 추가 */}
        <div>
          <label className="text-base font-semibold block mb-2">부스 이미지</label>
          <label htmlFor="posterImage" className="cursor-pointer w-full h-40 bg-gray-100 border-2 border-dashed rounded-lg flex flex-col items-center justify-center hover:bg-gray-200 text-gray-500 overflow-hidden">
            {previewBoothUrl ? (
              <Image src={previewBoothUrl} alt="미리보기" className="object-contain w-full h-full" />
            ) : (
              <>
                + 이미지 선택
                {form.posterImage && (
                  <p className="text-sm text-black mt-2">{form.posterImage.name}</p>
                )}
              </>
            )}
          </label>
          <input
            id="posterImage"
            name="posterImage"
            type="file"
            accept="image/*"
            className="hidden"
            onChange={handleImageChange}
          />
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
        <div>
          <label className="text-base font-semibold block mb-2">이벤트 이미지</label>
          <label htmlFor="eventImage" className="cursor-pointer w-full h-40 bg-gray-100 border-2 border-dashed rounded-lg flex flex-col items-center justify-center hover:bg-gray-200 text-gray-500 overflow-hidden">
            {previewEventUrl ? (
              <img src={previewEventUrl} alt="미리보기" className="object-contain w-full h-full" />
            ) : (
              <>
                + 이미지 선택
                {form.eventImage && (
                  <p className="text-sm text-black mt-2">{form.eventImage.name}</p>
                )}
              </>
            )}
          </label>
          <input
            id="eventImage"
            name="eventImage"
            type="file"
            accept="image/*"
            className="hidden"
            onChange={handleImageChange}
          />
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
