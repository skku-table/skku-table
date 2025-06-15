'use client';

import { useEffect } from 'react';
import { urlB64ToUint8Array } from '@/libs/utils';
import { getMessaging, getToken } from 'firebase/messaging';
import { messaging } from '@/libs/firebase';

export const usePushRegister = (userId: number) => {
  useEffect(() => {
    const register = async () => {
      if (!('serviceWorker' in navigator) || !('PushManager' in window)) {
        console.log('푸시 미지원 브라우저');
        return;
      }

      console.log('usePushRegister 실행됨');

      // 1. 알림 권한 요청
      const permission = await Notification.requestPermission();
      console.log('권한 요청 결과:', permission);
      if (permission !== 'granted') {
        console.log('알림 권한 거부됨');
        return;
      }

      try {
        // 2. 서비스 워커 등록
        const registration = await navigator.serviceWorker.register('/firebase-messaging-sw.js');
        await navigator.serviceWorker.ready;
        console.log('서비스 워커 등록 성공:', registration);


        //기존 구독이 있다면 먼저 해제
        const existingSubscription = await registration.pushManager.getSubscription();
        if (existingSubscription) {
          console.log('기존 구독이 존재함 → unsubscribe() 실행');
          await existingSubscription.unsubscribe();
        }

        // 4. FCM 토큰 요청
        if (messaging) {
          const token = await getToken(messaging, {
            vapidKey: process.env.NEXT_PUBLIC_FIREBASE_VAPID_PUBLIC_KEY!,
            serviceWorkerRegistration: registration,
          });
          console.log('✅ FCM Token:', token);

          // ✅ 5. 서버로 FCM 토큰 전송
          await fetch('/api/save-token', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userId, fcmToken: token }),
          });
        } else {
          console.warn('messaging 객체가 null입니다.');
        }        

        // 4. Push 구독 (선택)
        const applicationServerKey = urlB64ToUint8Array(process.env.NEXT_PUBLIC_VAPID_PUBLIC_KEY!);
        const subscription = await registration.pushManager.subscribe({
          userVisibleOnly: true,
          applicationServerKey,
        });
        console.log('pushSubscription:', subscription);
      } catch (err) {
        console.error('등록 중 오류:', err);
      }
    };

    register();
  }, []);
};
