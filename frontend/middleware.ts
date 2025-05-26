// middleware.ts
import { Session } from 'inspector/promises'
import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

export async function middleware(request: NextRequest) {
  const session = request.cookies.get('JSESSIONID')?.value
  const url = request.nextUrl
  const pathname = url.pathname

  const publicPaths = ['/login', '/signup']

  const isPublic = publicPaths.some((path) => url.pathname.startsWith(path))
  console.log('미들웨어 작동중..')

  // 로그인이 안 됐고, 보호된 경로로 접근한 경우
  if (!session && !isPublic) {
    return NextResponse.redirect(new URL('/login', request.url))
  }
  if (session && pathname.startsWith('/admin')){
    try {
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/users/me`, {
        headers: {
          'Cookie': `JSESSIONID=${session}`,
        },
      })
      if (res.ok){
        const user = await res.json()
        const role = user.role

        if (role !== 'ADMIN') {
          return NextResponse.redirect(new URL('/', request.url))
        }
      } else {
        console.error('사용자 정보 조회 실패:', res.status, res.statusText)
        return NextResponse.redirect(new URL('/login', request.url))
      }
    } catch (error){
      console.error('사용자 정보 조회 중 오류 발생:', error)
      return NextResponse.redirect(new URL('/login', request.url))
    }
  }

  return NextResponse.next()
}

// ✅ "/" 포함 모든 경로를 감시하게 설정
export const config = {
  matcher: [
    '/((?!_next/static|_next/image|favicon.ico|.*\\..*).*)',
  ],
}
