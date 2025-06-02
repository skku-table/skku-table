package com.skkutable.repository;

import com.skkutable.domain.UserBoothLike;
import com.skkutable.domain.UserFestivalLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


import java.util.List;

@Repository
public interface UserFestivalLikeRepository extends JpaRepository<UserFestivalLike, Long> {
    List<UserFestivalLike> findByUserId(Long userId);
    Optional<UserFestivalLike> findByUserIdAndFestivalId(Long userId, Long festivalId);
    boolean existsByUserIdAndFestivalId(Long userId, Long festivalId);
}
