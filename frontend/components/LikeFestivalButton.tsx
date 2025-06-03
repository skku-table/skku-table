'use client'

import { useState, useEffect } from 'react'
import { IoHeartOutline, IoHeartSharp } from 'react-icons/io5'
import { fetchWithCredentials } from '@/libs/fetchWithCredentials'

type LikeButtonProps = {
  festivalId: number
}

export default function LikeFestivalButton({ festivalId }: LikeButtonProps) {
  const [likedFestivalIds, setLikedFestivalIds] = useState<number[]>([])
  const [loading, setLoading] = useState(false)
  const [userId, setUserId] = useState<number | null>(null)

  // 사용자 정보 가져오기 (클라이언트 사이드에서)
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const userRes = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/users/me`, {
          method: 'GET',
          credentials: 'include',
        })

        if (userRes.ok) {
          const userData = await userRes.json()
          setUserId(userData.id)
        }
      } catch {
        console.error('사용자 정보 조회 실패')
      }
    }

    fetchUser()
  }, [])

  // 좋아요 목록 가져오기
  const fetchLikedFestivals = async (userId: number) => {
    try {
      const res = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/users/${userId}/likes/festivals`, {
        credentials: 'include',
      })
      if (res.ok) {
        const data = await res.json()
        const festivalIds = data.map((festival: { id: number }) => festival.id)
        console.log('좋아요 목록:', festivalIds)
        setLikedFestivalIds(festivalIds)
      }
    } catch {
      console.error('좋아요 목록 조회 실패')
    }
  }

  // 사용자 ID를 기준으로 좋아요 목록 가져오기
  useEffect(() => {
    if (userId !== null) {
      fetchLikedFestivals(userId)
      console.log('좋아요 목록 가져오기 완료:', likedFestivalIds)
    }
  }, [userId])

  // 좋아요 토글
  const handleClick = async () => {
    if (!userId) return
    setLoading(true)

    try {
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/users/${userId}/likes/festivals/${festivalId}/toggle`,
        {
          method: 'POST',
          credentials: 'include',
        }
      )

      if (res.ok) {
        // 좋아요 목록 다시 가져오기 (즉시 반영)
        await fetchLikedFestivals(userId)
      }
    } catch (err) {
      console.error('좋아요 토글 실패:', err)
    } finally {
      setLoading(false)
    }
  }

  const isLiked = likedFestivalIds.includes(festivalId)
  const Icon = isLiked ? IoHeartSharp : IoHeartOutline

  return (
    <button
      onClick={handleClick}
      className="absolute top-3 right-3 p-0 bg-transparent border-none"
      style={{ lineHeight: 0 }}
      disabled={loading || userId === null}
    >
      <Icon
        width={25}
        height={25}
        style={{
          color: isLiked ? 'red' : 'white',
        }}
      />
    </button>
  )
}
