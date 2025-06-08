package com.skkutable.service;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.domain.User;
import com.skkutable.domain.UserBoothLike;
import com.skkutable.domain.UserFestivalLike;
import com.skkutable.repository.UserBoothLikeRepository;
import com.skkutable.repository.UserFestivalLikeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserLikeService {

  private final UserService userService;
  private final FestivalService festivalService;
  private final BoothService boothService;
  private final UserFestivalLikeRepository userFestivalLikeRepo;
  private final UserBoothLikeRepository userBoothLikeRepo;

  @Transactional
  public boolean toggleFestivalLike(Long userId, Long festivalId) {
    User user = userService.findOne(userId);
    Festival festival = festivalService.findFestivalById(festivalId);

    var existingLike = userFestivalLikeRepo.findByUserIdAndFestivalId(userId, festivalId);
    if (existingLike.isPresent()) {
      // 취소 처리
      userFestivalLikeRepo.delete(existingLike.get());
      festival.decrementLikeCount();
      festivalService.save(festival);
      return false;
    } else {
      // 좋아요 처리
      userFestivalLikeRepo.save(UserFestivalLike.builder()
          .user(user)
          .festival(festival)
          .build());
      festival.incrementLikeCount();
      festivalService.save(festival);
      return true;
    }
  }

  @Transactional
  public boolean toggleBoothLike(Long userId, Long boothId) {
    User user = userService.findOne(userId);
    Booth booth = boothService.findBoothById(boothId);

    var existingLike = userBoothLikeRepo.findByUserIdAndBoothId(userId, boothId);
    if (existingLike.isPresent()) {
      // 취소 처리
      userBoothLikeRepo.delete(existingLike.get());
      booth.decrementLikeCount();
      boothService.save(booth);
      return false;
    } else {
      // 좋아요 처리
      userBoothLikeRepo.save(UserBoothLike.builder()
          .user(user)
          .booth(booth)
          .build());
      booth.incrementLikeCount();
      boothService.save(booth);
      return true;
    }
  }

  public List<Festival> getLikedFestivals(Long userId) {
    return userFestivalLikeRepo.findByUserId(userId).stream()
        .map(UserFestivalLike::getFestival)
        .toList();
  }

  public List<Booth> getLikedBooths(Long userId) {
    return userBoothLikeRepo.findByUserId(userId).stream()
        .map(UserBoothLike::getBooth)
        .toList();
  }
}

