package com.skkutable.controller;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.service.UserLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users/{userId}/likes")
@RequiredArgsConstructor
public class UserLikeController {

    private final UserLikeService userLikeService;

    @GetMapping("/festivals")
    public List<Festival> getLikedFestivals(@PathVariable Long userId) {
        return userLikeService.getLikedFestivals(userId);
    }

    @GetMapping("/booths")
    public List<Booth> getLikedBooths(@PathVariable Long userId) {
        return userLikeService.getLikedBooths(userId);
    }

    @PostMapping("/festivals/{festivalId}/toggle")
    public ResponseEntity<Map<String, Object>> toggleFestivalLike(@PathVariable Long userId, @PathVariable Long festivalId) {
        boolean isLiked = userLikeService.toggleFestivalLike(userId, festivalId);
        Map<String, Object> response = Map.of(
                "isLiked", isLiked,
                "message", isLiked ? "Festival liked" : "Festival unliked"
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/booths/{boothId}/toggle")
    public ResponseEntity<Map<String, Object>> toggleBoothLike(@PathVariable Long userId, @PathVariable Long boothId) {
        boolean isLiked = userLikeService.toggleBoothLike(userId, boothId);
        Map<String, Object> response = Map.of(
                "isLiked", isLiked,
                "message", isLiked ? "Booth liked" : "Booth unliked"
        );
        return ResponseEntity.ok(response);
    }
}

