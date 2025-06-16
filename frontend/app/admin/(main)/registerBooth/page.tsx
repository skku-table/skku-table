'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import Header from '@/components/Headers'
import { fetchWithCredentials } from '@/libs/fetchWithCredentials'
import Image from 'next/image'

import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
  } from "@/components/ui/select"

export default function RegisterBoothPage() {
  const router = useRouter()
  const [festivals, setFestivals] = useState<{ id: string, name: string }[]>([])
  const [previewEventUrl, setPreviewEventUrl] = useState<string | null>(null)
  const [previewBoothUrl, setPreviewBoothUrl] = useState<string | null>(null)
  const [dateList, setDateList] = useState<string[]>([])
  const [timeList, setTimeList] = useState<string[]>([])
  // const [selectedDate, setSelectedDate] = useState('')
  // const [selectedStartTime, setSelectedStartTime] = useState('')
  // const [selectedEndTime, setSelectedEndTime] = useState('')
  // const [maxCapacity, setMaxCapacity] = useState<number | ''>('')
  const [_, setSelectedDate] = useState('');
  const [__, setSelectedStartTime] = useState('');
  const [___, setSelectedEndTime] = useState('');
  const [timeSlots, setTimeSlots] = useState<
    { date: string; time: string; endTime: string; capacity: number | '' }[]
  >([])

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

  useEffect(() => {
  if (!form.startDateTime && !form.endDateTime) {
    const now = new Date()
    now.setHours(12 - now.getTimezoneOffset() / 60, 0, 0, 0)
    const end = new Date(now)
    end.setHours(21 - now.getTimezoneOffset() / 60, 0, 0, 0)

    setForm(prev => ({
      ...prev,
      startDateTime: now.toISOString().slice(0, 16),  // 'YYYY-MM-DDTHH:mm'
      endDateTime: end.toISOString().slice(0, 16),
    }))
  }
}, [])

  useEffect(() => {
    if (form.startDateTime && form.endDateTime) {
      const start = new Date(form.startDateTime)
      const end = new Date(form.endDateTime)
      const dates: string[] = []
      for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
        dates.push(new Date(d).toISOString().split('T')[0])
      }
      setDateList(dates)
      setSelectedDate(dates[0])

      const startTime = form.startDateTime.split('T')[1].slice(0, 5)
      const endTime = form.endDateTime.split('T')[1].slice(0, 5)
      const slots: string[] = []
      const startDate = new Date(`1970-01-01T${startTime}`)
      const endDate = new Date(`1970-01-01T${endTime}`)
      const current = new Date(startDate)
      while (current <= endDate) {
        const hh = current.getHours().toString().padStart(2, '0')
        const mm = current.getMinutes().toString().padStart(2, '0')
        slots.push(`${hh}:${mm}`)
        current.setMinutes(current.getMinutes() + 30)
      }
      setTimeList(slots)
      setSelectedStartTime(startTime)
      setSelectedEndTime(endTime)
    }
  }, [form.startDateTime, form.endDateTime])

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
      const url = URL.createObjectURL(file)
      if (name === 'eventImage') setPreviewEventUrl(url)
      if (name === 'posterImage') setPreviewBoothUrl(url)
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
      const data = await res.json();
      const boothId = data.id; // 부스 등록 후 받은 ID

      // 타임 슬롯들 순회하며 POST
      for (const slot of timeSlots) {
        if (!slot.date || !slot.time) {
          console.error('타임 슬롯 데이터 오류:', slot);
          alert('모든 타임 슬롯에 날짜와 시간이 입력되어야 합니다.');
          return;
        }
        const startTime = `${slot.date}T${slot.time}:00`;
        const endTime = `${slot.date}T${slot.endTime}:00`;

        await fetchWithCredentials(
          `${process.env.NEXT_PUBLIC_API_URL}/booths/${boothId}/timeslots`,
          {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({
              startTime: startTime,
              endTime: endTime,
              maxCapacity: slot.capacity,
            }),
          }
        )
      //   console.log('보내는 슬롯 데이터:', {
      //   startTime,
      //   endTime,
      //   maxCapacity: slot.capacity
      // });  
      }
   
      



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
              <Image src={previewBoothUrl} alt="미리보기" width={300} height={300} className="object-contain w-full h-full" />
            ) : (
              <>+ 이미지 선택</>) }
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
            <Select onValueChange={(value) => setForm({ ...form, festivalId: value })}>
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

        {/* 슬롯 및 수용 인원 정보 */}
        {dateList.length > 0 && (
        <div className="space-y-4">
          <h3 className="text-base font-semibold text-black mb-1">
            타임 슬롯 생성
          </h3>

          {timeSlots.map((slot, index) => (
            <div key={index} className="space-y-2">
              {/* 첫 번째 줄*/}

              <div className="flex gap-4">
                {/* 날짜 */}
                <select
                  value={slot.date}
                  onChange={(e) => {
                    const updated = [...timeSlots];
                    updated[index].date = e.target.value;
                    setTimeSlots(updated);
                  }}
                  className="w-[40%] border border-gray-300 rounded-md p-2"
                >
                  <option value="" disabled hidden>날짜</option>
                  {dateList.map((date) => (
                    <option key={date} value={date}>
                      {date}
                    </option>
                  ))}
                </select>

                {/* 슬롯 취소 버튼 */}
                <button
                  type="button"
                  onClick={() => {
                    const updated = [...timeSlots];
                    updated.splice(index, 1);
                    setTimeSlots(updated);
                  }}
                  className="ml-auto text-blue-500 underline text-sm"
                >
                  슬롯 취소
                </button>
              
              </div>
            

              {/* 두 번째 줄 */}
              <div className="flex gap-2">
                {/* 시작 시간 */}
                <select
                  value={slot.time}
                  onChange={(e) => {
                    const updated = [...timeSlots];
                    updated[index].time = e.target.value;
                    setTimeSlots(updated);
                  }}
                  className="w-[35%] border border-gray-300 rounded-md p-2"
                >
                  <option value="" disabled hidden>시작 시간</option>
                  {timeList.map((time) => (
                    <option key={time} value={time}>{time}</option>
                  ))}
                </select>

                {/* 종료 시간 */}
                <select
                  value={slot.endTime || ''}
                  onChange={(e) => {
                    const updated = [...timeSlots];
                    updated[index].endTime = e.target.value;
                    setTimeSlots(updated);
                  }}
                  className="w-[35%] border border-gray-300 rounded-md p-2"
                >
                  <option value="" disabled hidden>종료 시간</option>
                  {timeList.map((time) => (
                    <option key={time} value={time}>{time}</option>
                  ))}
                </select>
                 
                {/* 수용 인원 */}
                <input
                  type="number"
                  min={1}
                  value={slot.capacity}
                  placeholder="수용 인원"
                  onChange={(e) => {
                    const updated = [...timeSlots];
                    updated[index].capacity = Number(e.target.value);
                    setTimeSlots(updated);
                  }}
                  className="w-[25%] border border-gray-300 rounded-md p-2"
                />          
              </div>

            </div>
          ))}

          {/* 타임슬롯 추가 버튼 */}
          <button
            type="button"
            onClick={() =>
              setTimeSlots([
                ...timeSlots,
                {
                  date: '',
                  time: '',
                  endTime: '',
                  capacity: ''
                }
              ])
            }
            className="text-sm text-blue-600 hover:underline"
          >
            + 타임 슬롯 추가
          </button>
        </div>
      )}

        {/* 상세 정보 */}
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
              <Image src={previewEventUrl} alt="미리보기" width={300} height={300} className="object-contain w-full h-full" />
            ) : (
              <>+ 이미지 선택</>
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
