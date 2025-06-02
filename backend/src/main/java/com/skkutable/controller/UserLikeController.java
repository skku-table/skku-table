package com.skkutable.controller;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.service.UserLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Void> toggleFestivalLike(@PathVariable Long userId, @PathVariable Long festivalId) {
        userLikeService.toggleFestivalLike(userId, festivalId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/booths/{boothId}/toggle")
    public ResponseEntity<Void> toggleBoothLike(@PathVariable Long userId, @PathVariable Long boothId) {
        userLikeService.toggleBoothLike(userId, boothId);
        return ResponseEntity.ok().build();
    }
}

