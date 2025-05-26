export async function fetchWithCredentials(
    input: RequestInfo | URL,
    init?: RequestInit
  ) {
    return fetch(input, {
      ...init,
      credentials: 'include',
    
    })
  }
  