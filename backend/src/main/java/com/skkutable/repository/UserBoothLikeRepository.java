package com.skkutable.repository;

import com.skkutable.domain.UserBoothLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBoothLikeRepository extends JpaRepository<UserBoothLike, Long> {

  List<UserBoothLike> findByUserId(Long userId);

  Optional<UserBoothLike> findByUserIdAndBoothId(Long userId, Long boothId);

  boolean existsByUserIdAndBoothId(Long userId, Long boothId);
}
