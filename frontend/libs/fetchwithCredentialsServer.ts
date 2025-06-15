// 수정된 fetchWithCredentials.ts
import { cookies } from "next/headers";

export async function fetchWithCredentials(
  input: RequestInfo | URL,
  init?: RequestInit
) {
  const cookieHeader = cookies().toString();

  return fetch(input, {
    ...init,
    headers: {
      ...init?.headers,
      Cookie: cookieHeader, // <- 여기 추가
    },
    cache: "no-store", // SSR 환경에서는 보통 cache 끄는 게 안전
  });
}
