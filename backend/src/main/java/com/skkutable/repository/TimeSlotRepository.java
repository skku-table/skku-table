package com.skkutable.repository;

import com.skkutable.domain.TimeSlot;
import com.skkutable.domain.TimeSlotStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
  
  List<TimeSlot> findByBoothId(Long boothId);
  
  List<TimeSlot> findByBoothIdAndStatus(Long boothId, TimeSlotStatus status);
  
  Optional<TimeSlot> findByIdAndBoothId(Long id, Long boothId);
  
  @Query("SELECT ts FROM TimeSlot ts WHERE ts.booth.id = :boothId " +
         "AND ts.startTime >= :startTime AND ts.endTime <= :endTime")
  List<TimeSlot> findByBoothIdAndTimeBetween(@Param("boothId") Long boothId,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);
  
  @Query("SELECT ts FROM TimeSlot ts WHERE ts.booth.id = :boothId " +
         "AND ts.status = 'AVAILABLE' AND ts.currentCapacity < ts.maxCapacity")
  List<TimeSlot> findAvailableTimeSlotsByBoothId(@Param("boothId") Long boothId);
  
  boolean existsByBoothIdAndStartTimeAndEndTime(Long boothId, LocalDateTime startTime, LocalDateTime endTime);
}
