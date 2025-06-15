package com.skkutable.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


@Component
public class FirebaseInitializer {

    @PostConstruct
    public void init() throws IOException {
        URL resourceUrl = getClass()
            .getClassLoader()
            .getResource("firebase/service-account.json");
        System.out.println("🔍 [DEBUG] Firebase JSON 위치: " + resourceUrl);
        // classpath로부터 JSON 파일 불러오기
        InputStream serviceAccount = getClass()
            .getClassLoader()
            .getResourceAsStream("firebase/service-account.json");

        if (serviceAccount == null) {
            throw new IllegalStateException("서비스 계정 JSON 파일을 찾을 수 없습니다.");
        }

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase 초기화 완료");
        }
    }
}

