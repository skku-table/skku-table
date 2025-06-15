package com.skkutable.repository.custom;

import com.skkutable.domain.TimeSlot;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepositoryCustom {

  Optional<TimeSlot> findByIdWithLock(Long id);

  Optional<TimeSlot> findByIdAndBoothIdWithLock(Long id, Long boothId);

  /**
   * 특정 기간 내의 타임슬롯 조회
   */
  List<TimeSlot> findByBoothIdAndTimeBetween(Long boothId, LocalDateTime startTime,
      LocalDateTime endTime);

  /**
   * 예약 가능한 타임슬롯 조회
   */
  List<TimeSlot> findAvailableTimeSlotsByBoothId(Long boothId);

  boolean existsByBoothIdAndStartTimeAndEndTime(Long boothId, LocalDateTime startTime,
      LocalDateTime endTime);
}
