'use client'

import Header from "@/components/Headers"
import { useSearchParams } from "next/navigation"
import { useForm } from "react-hook-form"
import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"

type FormData = {
    name: string;
    email: string;
    password: string;
    role: string;
}
export default function SignupPage() {
    const router = useRouter()
    const searchParams = useSearchParams()
    const [error, setError] = useState('')
    const [isAdmin, setIsAdmin] = useState(false)
    useEffect(()=> {
        const value = searchParams.get('isAdmin')
        setIsAdmin(value === 'true')
    }, [searchParams])


    const { register, handleSubmit, formState: { errors } } = useForm<FormData>()
    const onSubmit = async (data: FormData) => {
        setError('')
        const role = isAdmin ? 'ADMIN' : 'USER'
        const payload = { ...data, role }
        try {
            const body = JSON.stringify(payload)
            const headers: HeadersInit = {
                'Content-Type': 'application/json',
            }
            if (isAdmin) {
                headers['x-admin-secret'] = process.env.NEXT_PUBLIC_ADMIN_SECRET!
            }
            const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/users/signup`, {
                method: 'POST',
                credentials: 'include',
                headers,
                body,
            })

            if (!res.ok) {
                const text = await res.text()
                console.error('회원가입 실패 응답:', text)
                setError('회원가입 실패: ' + text)
                return
            }
            router.push('/login')
        } catch (err) {
            setError('서버 통신 중 오류 발생')
            console.error(err)
        }
    }


    return (
        <>
        <Header isBackButton={true} title="back" />
        <main className="flex flex-col items-center py-40 min-h-screen bg-white">
            <h1 className="text-2xl font-bold text-center mb-10">
            {isAdmin ? '관리자 회원가입' : '일반 사용자 회원가입'}
            </h1>

            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 w-2/3">
            <div>
                <label className="block text-sm">이름</label>
                <input
                {...register('name', { required: '이름을 입력하세요' })}
                className="w-full border p-2 rounded mt-1"
                placeholder="이름을 입력하세요"
                />
                {errors.name && <p className="text-red-500 text-sm mt-1">{errors.name.message}</p>}
            </div>

            <div>
                <label className="block text-sm">이메일</label>
                <input
                {...register('email', {
                    required: '이메일을 입력하세요',
                    pattern: {
                    value: /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/,
                    message: '유효한 이메일 형식이 아닙니다',
                    },
                })}
                className="w-full border p-2 rounded mt-1"
                placeholder="이메일을 입력하세요"
                />
                {errors.email && <p className="text-red-500 text-sm mt-1">{errors.email.message}</p>}
            </div>

            <div>
                <label className="block text-sm">비밀번호</label>
                <input
                type="password"
                {...register('password', { required: '비밀번호를 입력하세요' })}
                className="w-full border p-2 rounded mt-1"
                placeholder="비밀번호를 입력하세요"
                />
                {errors.password && <p className="text-red-500 text-sm mt-1">{errors.password.message}</p>}
            </div>

            <button
                type="submit"
                className="w-full mt-10 bg-[#334433] hover:bg-[#496249] text-white font-semibold py-2 rounded"
            >
                회원가입
            </button>
            </form>

            {error && <p className="text-red-500 text-center mt-4 text-sm">{error}</p>}

        </main>
        </>
    )
}