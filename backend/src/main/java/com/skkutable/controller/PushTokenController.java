package com.skkutable.controller;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.skkutable.dto.PushTokenRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PushTokenController {

    @PostMapping("/save-token")
    public ResponseEntity<String> saveToken(@RequestBody PushTokenRequest request) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            Map<String, Object> data = new HashMap<>();
            data.put("userId", request.getUserId());
            data.put("fcmToken", request.getFcmToken());

            // Firestore의 users 컬렉션에 저장 (userId를 docId로 사용)
            db.collection("users")
              .document(String.valueOf(request.getUserId()))
              .set(data);

            return ResponseEntity.ok("✅ FCM 토큰 저장 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ FCM 토큰 저장 실패");
        }
    }
}