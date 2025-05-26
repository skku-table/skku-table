// middleware.ts
import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

export function middleware(request: NextRequest) {
  const token = request.cookies.get('token')?.value
  const url = request.nextUrl

  const publicPaths = ['/login', '/signup']

  const isPublic = publicPaths.some((path) => url.pathname.startsWith(path))
  console.log('미들웨어 작동중..')

  // 로그인이 안 됐고, 보호된 경로로 접근한 경우
  if (!token && !isPublic) {
    return NextResponse.redirect(new URL('/login', request.url))
  }

  return NextResponse.next()
}

// ✅ "/" 포함 모든 경로를 감시하게 설정
export const config = {
  matcher: [
    '/((?!_next/static|_next/image|favicon.ico|.*\\..*).*)',
  ],
}
