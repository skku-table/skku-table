// Firebase SDK의 compat 버전을 서비스워커에서 사용
importScripts('https://www.gstatic.com/firebasejs/9.20.0/firebase-app-compat.js')
importScripts('https://www.gstatic.com/firebasejs/9.20.0/firebase-messaging-compat.js')

// Firebase 프로젝트 설정
const firebaseConfig = {
  apiKey: "AIzaSyBesfvpK2f32v-ht89cVkU0TjWlNs4wSMQ",
  authDomain: "skku-table.firebaseapp.com",
  projectId: "skku-table",
  storageBucket: "skku-table.firebasestorage.app",
  messagingSenderId: "470691960790",
  appId: "1:470691960790:web:9393f539a10c1cf8603677",
  measurementId: "G-Y8NVRWHKM8"
};

// Firebase 초기화
firebase.initializeApp(firebaseConfig)

// Messaging
const messaging = firebase.messaging()
messaging.onBackgroundMessage(function (payload) {
  console.log('[firebase-messaging-sw.js] 백그라운드 메시지 수신: ', payload);
  const notificationTitle = payload.notification.title;
  const notificationOptions = {
    body: payload.notification.body,
    icon: '/icon-rectangle.png', // 아이콘 경로 -> 나중에 추가하기...
    data: {
      link: 'https://skkutable.com', // 알림 클릭 시 이동할 URL
    },
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});
