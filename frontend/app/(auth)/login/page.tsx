'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'

export default function LoginPage() {
  const router = useRouter()
  const [id, setId] = useState('')
  const [password, setPassword] = useState('')
  const [isAdmin, setIsAdmin] = useState(false)
  const [error, setError] = useState('')

  const handleLogin = async () => {
    setError('')

    try {
      const body = new URLSearchParams({ id, password }).toString()
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/users/login`, {
        method: 'POST',
        credentials: 'include', // 세션/쿠키 기반 인증일 경우 꼭 필요!
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
        body,
      })

      if (!res.ok) {
        setError('로그인 실패: 아이디 또는 비밀번호 오류')
        return
      }

      const data = await res.json()

      const role = data.role
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
    <main className="flex flex-col items-center justify-center min-h-screen bg-gray-50">
      <div className="w-full max-w-sm p-6 bg-white rounded shadow-md">
        <h1 className="text-2xl font-bold text-center mb-4">
          {isAdmin ? '관리자 로그인' : '일반 사용자 로그인'}
        </h1>

        <label className="block mb-2">
          <span className="text-sm">아이디</span>
          <input
            className="w-full border p-2 rounded mt-1"
            value={id}
            onChange={(e) => setId(e.target.value)}
            placeholder="아이디를 입력하세요"
          />
        </label>

        <label className="block mb-4">
          <span className="text-sm">비밀번호</span>
          <input
            type="password"
            className="w-full border p-2 rounded mt-1"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="비밀번호를 입력하세요"
          />
        </label>

        <button
          className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 rounded mb-3"
          onClick={handleLogin}
        >
          로그인
        </button>

        <div className="text-center text-sm text-gray-600 mb-3">
          {isAdmin ? '관리자 계정으로 로그인 중입니다.' : '일반 사용자 계정으로 로그인 중입니다.'}
        </div>

        <button
          className="text-sm underline text-blue-600"
          onClick={() => setIsAdmin(!isAdmin)}
        >
          {isAdmin ? '일반 사용자로 전환' : '관리자로 전환'}
        </button>

        {error && <p className="text-red-500 text-center mt-4">{error}</p>}
      </div>
    </main>
  )
}
