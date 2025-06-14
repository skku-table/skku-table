import { initializeApp, getApps, getApp } from 'firebase/app'
import { getMessaging } from 'firebase/messaging'

// Firebase 설정
const firebaseConfig = {
  apiKey: process.env.NEXT_PUBLIC_API_KEY, 
  authDomain: process.env.NEXT_PUBLIC_AUTH_DOMAIN,
  projectId: process.env.NEXT_PUBLIC_PROJECT_ID,
  storageBucket: process.env.NEXT_PUBLIC_STORAGE_BUCKET,
  messagingSenderId: process.env.NEXT_PUBLIC_MESSAGING_SENDER_ID,
  appId: process.env.NEXT_PUBLIC_APP_ID,
  measurementId: process.env.NEXT_PUBLIC_MEASUREMENT_ID,
};
// Firebase 초기화
const app = getApps().length === 0 ? initializeApp(firebaseConfig) : getApp()

// messaging 객체 export
export const messaging = typeof window !== 'undefined' ? getMessaging(app) : null

export default app
