'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { fetchWithCredentials } from '@/libs/fetchWithCredentials'

export default function LoginPage() {
  const router = useRouter()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [isAdmin, setIsAdmin] = useState(false)
  const [error, setError] = useState('')

  const handleLogin = async () => {
    setError('')
    try {
      const body = new URLSearchParams({ email, password }).toString()
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/users/login`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body,
      })
      const text = await res.text()
      if (!res.ok) {
        console.error('로그인 실패 응답:', text)
        setError('로그인 실패: 아이디 또는 비밀번호 오류')
        return
      }

      const userRes = await fetchWithCredentials(`${process.env.NEXT_PUBLIC_API_URL}/users/me`, {
        method: 'GET',
        credentials: 'include',
      })

      if (!userRes.ok) {
        setError('로그인 후 사용자 정보 조회 실패')
        return
      }

      const userData = await userRes.json()
      const role = userData.role

      if (role === 'USER') {
        router.push('/')
      } else if (role === 'ADMIN') {
        router.push('/admin')
      } else {
        setError('알 수 없는 사용자 유형입니다.')
      }
    } catch (err) {
      setError('서버 통신 중 오류 발생')
      console.error(err)
    }
  }

  return (
    <main className="flex flex-col items-center py-40 min-h-screen bg-white">
      <h1 className="text-4xl font-[900] pb-15 text-[#334433]">SKKU TABLE</h1>

      <div className="w-full max-w-xs p-4">
        <div className="flex mb-10 border-b border-[#334433]">
          <button
            onClick={() => setIsAdmin(false)}
            className={`flex-1 py-2 text-sm font-medium ${
              !isAdmin ? 'border-b-4 border-[#334433] text-black' : 'text-gray-500'
            }`}
          >
            일반사용자 계정
          </button>
          <button
            onClick={() => setIsAdmin(true)}
            className={`flex-1 py-2 text-sm font-medium ${
              isAdmin ? 'border-b-4 border-[#334433] text-black' : 'text-gray-500'
            }`}
          >
            관리자 계정
          </button>
        </div>

        <label className="block mb-4">
          <input
            className="w-full border border-gray-300 p-3 rounded-md text-sm placeholder-gray-400"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="이메일을 입력하세요"
          />
        </label>

        <label className="block mb-4 relative">
          <input
            type="password"
            className="w-full border border-gray-300 p-3 rounded-md text-sm placeholder-gray-400"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="비밀번호를 입력하세요"
          />
          <span className="absolute right-2 bottom-[-20px] text-xs text-gray-500 hover:underline cursor-pointer">
            회원가입
          </span>
        </label>

        <button
          className="w-full bg-[#334433] hover:cursor-pointer hover:bg-[#496249] text-white font-semibold py-3 mt-10 rounded-md"
          onClick={handleLogin}
        >
          로그인
        </button>

        {error && <p className="text-red-500 text-center text-sm mt-4">{error}</p>}
      </div>
    </main>
  )
}
