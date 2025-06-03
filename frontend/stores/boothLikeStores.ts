import { create } from 'zustand'
import { fetchWithCredentials } from '@/libs/fetchWithCredentials'


type LikeStore = {
  userId: number | null
  likedBoothIds: number[]
  boothLikeCounts: Record<number, number>
  setUserId: (id: number) => void
  setLikedBoothIds: (ids: number[]) => void
  setBoothLikeCount: (boothId: number, count: number) => void
  fetchUserAndLikes: () => Promise<void>
}
type BoothLikeData = {
  id: number
  likeCount: number
}

export const boothLikeStore = create<LikeStore>((set) => ({
  userId: null,
  likedBoothIds: [],
  boothLikeCounts: {},

  setUserId: (id) => set({ userId: id }),
  setLikedBoothIds: (ids) => set({ likedBoothIds: ids }),
  setBoothLikeCount: (boothId, count) =>
    set((state) => ({
      boothLikeCounts: {
        ...state.boothLikeCounts,
        [boothId]: count,
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
        `${process.env.NEXT_PUBLIC_API_URL}/users/${userId}/likes/booths`,
        {
          method: 'GET',
          credentials: 'include',
        }
      )
      const likesData = await likesRes.json()

      set({
        likedBoothIds: likesData.map((f: BoothLikeData) => f.id),
        boothLikeCounts: Object.fromEntries(
          likesData.map((f: BoothLikeData) => [f.id, f.likeCount])
        ),
      })
    } catch (e) {
      console.error('좋아요 목록 fetch 실패:', e)
    }
  },
}))
