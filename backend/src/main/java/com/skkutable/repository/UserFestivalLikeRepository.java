package com.skkutable.repository;

import com.skkutable.domain.UserFestivalLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFestivalLikeRepository extends JpaRepository<UserFestivalLike, Long> {

  List<UserFestivalLike> findByUserId(Long userId);

  Optional<UserFestivalLike> findByUserIdAndFestivalId(Long userId, Long festivalId);

  boolean existsByUserIdAndFestivalId(Long userId, Long festivalId);
}
