// stores/useLikeStore.ts
import { create } from 'zustand'
import { fetchWithCredentials } from '@/libs/fetchWithCredentials'


type LikeStore = {
  userId: number | null
  likedFestivalIds: number[]
  festivalLikeCounts: Record<number, number>
  setUserId: (id: number) => void
  setLikedFestivalIds: (ids: number[]) => void
  setFestivalLikeCount: (festivalId: number, count: number) => void
  fetchUserAndLikes: () => Promise<void>
}

export const useLikeStore = create<LikeStore>((set) => ({
  userId: null,
  likedFestivalIds: [],
  festivalLikeCounts: {},

  setUserId: (id) => set({ userId: id }),
  setLikedFestivalIds: (ids) => set({ likedFestivalIds: ids }),
  setFestivalLikeCount: (festivalId, count) =>
    set((state) => ({
      festivalLikeCounts: {
        ...state.festivalLikeCounts,
        [festivalId]: count,
      },
    })),

  fetchUserAndLikes: async () => {
    try {
      const userRes = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/users/me`, {
        method: 'GET',
        credentials: 'include',
      })
      if (!userRes.ok) throw new Error('유저 정보 요청 실패')
      const userData = await userRes.json()
      const userId = userData.id
      set({ userId })

      const likesRes = await fetchWithCredentials(
        `${process.env.NEXT_PUBLIC_API_URL}/users/${userId}/likes/festivals`,
        {
          method: 'GET',
          credentials: 'include',
        }
      )
      const likesData = await likesRes.json()

      set({
        likedFestivalIds: likesData.map((f: any) => f.id),
        festivalLikeCounts: Object.fromEntries(
          likesData.map((f: any) => [f.id, f.likeCount])
        ),
      })
    } catch (e) {
      console.error('좋아요 목록 fetch 실패:', e)
    }
  },
}))
