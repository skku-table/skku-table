package com.skkutable.repository;

import com.skkutable.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r JOIN FETCH r.booth b JOIN FETCH b.festival WHERE r.user.id = :userId")
    List<Reservation> findByUserIdWithBoothAndFestival(@Param("userId") Long userId);
    List<Reservation> findByBoothFestivalIdAndBoothId(Long festivalId, Long boothId);
}
