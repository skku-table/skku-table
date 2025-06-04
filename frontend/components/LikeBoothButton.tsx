'use client'

import { useState, useEffect } from 'react'
import { IoHeartOutline, IoHeartSharp } from 'react-icons/io5'
import { fetchWithCredentials } from '@/libs/fetchWithCredentials'
import { boothLikeStore } from '@/stores/boothLikeStores'

type LikeButtonProps = {
  boothId: number
}

export default function LikeBoothButton({ boothId }: LikeButtonProps) {
  const {
    userId,
    likedBoothIds,
    boothLikeCounts,
    setLikedBoothIds,
    setBoothLikeCount,
    fetchUserAndLikes,
  } = boothLikeStore()

  const [loading, setLoading] = useState(false)


  // 사용자 정보 및 좋아요 목록 가져오기
  useEffect(() => {
    if (userId === null) {
      fetchUserAndLikes()
    }
  }, [userId, fetchUserAndLikes])

  const isLiked = likedBoothIds.includes(boothId)
  const likeCount = boothLikeCounts[boothId] ?? 0
  const Icon = isLiked ? IoHeartSharp : IoHeartOutline

  // 좋아요 토글
  const handleClick = async () => {
    if (!userId) return
    setLoading(true)
    try {
      const res = await fetchWithCredentials(
        `${process.env.NEXT_PUBLIC_API_URL}/users/${userId}/likes/booths/${boothId}/toggle`,
        {
          method: 'POST',
          credentials: 'include',
        }
      )

      if (res.ok) {
        const data = await res.json()
        const isNowLiked = data.isLiked

        if (isNowLiked) {
          setLikedBoothIds([...likedBoothIds, boothId])
        } else {
          setLikedBoothIds(likedBoothIds.filter((id) => id !== boothId))
        }
        setBoothLikeCount(
          boothId,
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
      className="absolute top-1/15 right-1/15 p-0 w-1/8 h-1/8 bg-transparent border-none"
      style={{ lineHeight: 0 }}
      disabled={loading || userId === null}
    >
      <Icon
        width={50}
        height={50}
        style={{
          color: isLiked ? 'red' : 'white',
          width: '100%',
          height: '100%',
        }}
      />
    </button>
  )
}
