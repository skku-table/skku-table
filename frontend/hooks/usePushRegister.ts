'use client';

import { useEffect } from 'react';
import { urlB64ToUint8Array } from '@/libs/utils';
import { getMessaging, getToken } from 'firebase/messaging';
import { messaging } from '@/libs/firebase';

export const usePushRegister = () => {
  useEffect(() => {
    const register = async () => {
      if (!('serviceWorker' in navigator) || !('PushManager' in window)) {
        console.log('푸시 미지원 브라우저');
        return;
      }

      console.log('usePushRegister 실행됨');

      // FCM 토큰 요청 
       try {
        if (messaging) {
          const token = await getToken(messaging, {
            vapidKey: process.env.NEXT_PUBLIC_FIREBASE_VAPID_PUBLIC_KEY!,
          });
          console.log('FCM Token:', token);
        } else {
          console.warn('messaging 객체가 null입니다. (브라우저 환경 아님)');
        }
      } catch (err) {
        console.error('FCM 토큰 요청 실패:', err);
      }

      // 알림 권한 요청
      const permission = await Notification.requestPermission();
      console.log('권한 요청 결과:', permission);
      if (permission !== 'granted') {
        console.log('알림 권한 거부됨');
        return;
      }

      // 서비스워커 등록
      try {
        const registration = await navigator.serviceWorker.register('/sw.js');
        console.log('수동 등록 성공:', registration);

        const applicationServerKey = urlB64ToUint8Array(process.env.NEXT_PUBLIC_VAPID_PUBLIC_KEY!);
        const subscription = await registration.pushManager.subscribe({
          userVisibleOnly: true,
          applicationServerKey,
        });

        console.log('📨 pushSubscription:', subscription);
      } catch (err) {
        console.error('서비스 워커 등록/구독 실패:', err);
      }
    };

    register();
  }, []);
};
