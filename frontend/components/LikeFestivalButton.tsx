'use client'

import { useState, useEffect } from 'react'
import { IoHeartOutline, IoHeartSharp } from 'react-icons/io5'
import { fetchWithCredentials } from '@/libs/fetchWithCredentials'
import { useLikeStore } from '@/stores/useLikeStore'

type LikeButtonProps = {
  festivalId: number
}

export default function LikeFestivalButton({ festivalId }: LikeButtonProps) {
  const {
    userId,
    likedFestivalIds,
    festivalLikeCounts,
    setLikedFestivalIds,
    setFestivalLikeCount,
    fetchUserAndLikes,
  } = useLikeStore()

  const [loading, setLoading] = useState(false)

  // 사용자 정보 및 좋아요 목록 가져오기
  useEffect(() => {
    if (!userId) {
      fetchUserAndLikes()
    }
  }, [userId, fetchUserAndLikes])

  const isLiked = likedFestivalIds.includes(festivalId)
  const likeCount = festivalLikeCounts[festivalId] ?? 0
  const Icon = isLiked ? IoHeartSharp : IoHeartOutline

  // 좋아요 토글
  const handleClick = async () => {
    if (!userId) {
      console.warn('userId 없음. 다시 fetch 시도')
      await fetchUserAndLikes()
      return
    }
    setLoading(true)
    try {
      const res = await fetchWithCredentials(
        `${process.env.NEXT_PUBLIC_API_URL}/users/${userId}/likes/festivals/${festivalId}/toggle`,
        {
          method: 'POST',
          credentials: 'include',
        }
      )

      if (res.ok) {
        const data = await res.json()
        const isNowLiked = data.isLiked

        if (isNowLiked) {
          setLikedFestivalIds([...likedFestivalIds, festivalId])
        } else {
          setLikedFestivalIds(likedFestivalIds.filter((id) => id !== festivalId))
        }
        setFestivalLikeCount(
          festivalId,
          isNowLiked ? likeCount + 1 : likeCount - 1
        )
      }
    } catch (e) {
      console.error('좋아요 토글 실패:', e)
    } finally {
      setLoading(false)
    }
  }

  return (
    <button
      onClick={handleClick}
      className="absolute top-3 right-3 p-0 w-1/8 h-1/8 bg-transparent border-none"
      style={{ lineHeight: 0 }}
      disabled={loading}
    >
      <Icon
        width={125}
        height={125}
        style={{
          color: isLiked ? 'red' : 'white',
          width: '100%',
          height: '100%',
        }}
      />
    </button>
  )
}
