package com.skkutable.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class FirebaseInitializer {

    @Value("${firebase.service-account-key:}")
    private String firebaseServiceAccountKey;

    @PostConstruct
    public void init() throws IOException {
        // 환경 변수에서 Firebase 서비스 계정 키를 먼저 확인
        if (firebaseServiceAccountKey != null && !firebaseServiceAccountKey.trim().isEmpty()) {
            System.out.println("✅ [DEBUG] 환경 변수에서 Firebase 서비스 계정 키를 찾았습니다.");
            initializeFirebaseFromEnv();
        } else {
            // 환경 변수가 없으면 리소스 파일에서 시도 (로컬 개발용)
            System.out.println("🔍 [DEBUG] 환경 변수에 Firebase 키가 없습니다. 리소스 파일에서 찾는 중...");
            initializeFirebaseFromResource();
        }
    }

    private void initializeFirebaseFromEnv() throws IOException {
        try {
            InputStream serviceAccount = new ByteArrayInputStream(
                firebaseServiceAccountKey.getBytes(StandardCharsets.UTF_8)
            );

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase 초기화 완료 (환경 변수 사용)");
            }
        } catch (Exception e) {
            System.err.println("❌ 환경 변수를 통한 Firebase 초기화 실패: " + e.getMessage());
            throw new IllegalStateException("Firebase 환경 변수 초기화 실패", e);
        }
    }

    private void initializeFirebaseFromResource() throws IOException {
        InputStream serviceAccount = getClass()
            .getClassLoader()
            .getResourceAsStream("firebase/service-account.json");

        if (serviceAccount == null) {
            System.err.println("❌ Firebase 서비스 계정 파일을 찾을 수 없습니다.");
            System.err.println("💡 해결 방법:");
            System.err.println("   1. 환경 변수 FIREBASE_SERVICE_ACCOUNT_KEY 설정");
            System.err.println("   2. 또는 src/main/resources/firebase/service-account.json 파일 추가");
            throw new IllegalStateException("Firebase 서비스 계정 설정이 필요합니다.");
        }

        try {
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase 초기화 완료 (리소스 파일 사용)");
            }
        } catch (Exception e) {
            System.err.println("❌ 리소스 파일을 통한 Firebase 초기화 실패: " + e.getMessage());
            throw new IllegalStateException("Firebase 리소스 파일 초기화 실패", e);
        }
    }
}