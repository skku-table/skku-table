'use client';

import { useEffect } from 'react';
import { getToken } from 'firebase/messaging';
import { messaging } from '@/libs/firebase';

export const usePushRegister = (userId: number) => {
  useEffect(() => {
    const register = async () => {
      if (!('serviceWorker' in navigator) || !('PushManager' in window)) {
        console.log('🚫 푸시 알림을 지원하지 않는 브라우저입니다.');
        return;
      }

      const permission = await Notification.requestPermission();
      if (permission !== 'granted') return;

      try {
        const registration = await navigator.serviceWorker.register('/firebase-messaging-sw.js');
        await navigator.serviceWorker.ready;

        if (!messaging) {
          console.warn('⚠️ messaging 객체가 null입니다.');
          return;
        }

        const token = await getToken(messaging, {
          vapidKey: process.env.NEXT_PUBLIC_FIREBASE_VAPID_PUBLIC_KEY!,
          serviceWorkerRegistration: registration,
        });


        const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/save-token`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ userId, fcmToken: token }),
          credentials: 'include',
        });

        if (!res.ok) {
          const text = await res.text();
          throw new Error(`❌ 토큰 저장 실패: ${res.status} - ${text}`);
        }


      } catch (err) {
        console.error('❌ 등록 중 오류:', err);
      }
    };

    register();
  }, [userId]);
};
