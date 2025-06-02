package com.skkutable.repository;

import com.skkutable.domain.UserBoothLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


import java.util.List;

@Repository
public interface UserBoothLikeRepository extends JpaRepository<UserBoothLike, Long> {
    List<UserBoothLike> findByUserId(Long userId);
    Optional<UserBoothLike> findByUserIdAndBoothId(Long userId, Long boothId);
    boolean existsByUserIdAndBoothId(Long userId, Long boothId);
}
