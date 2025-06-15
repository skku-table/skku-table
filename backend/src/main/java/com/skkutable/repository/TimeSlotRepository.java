package com.skkutable.repository;

import com.skkutable.domain.TimeSlot;
import com.skkutable.domain.TimeSlotStatus;
import com.skkutable.repository.custom.TimeSlotRepositoryCustom;
import java.util.List;
import java.util.Optional;

/**
 * TimeSlot Repository 인터페이스 Custom query를 제외한 기본적인 JPA 메서드들을 명시적으로 선언 Festival Repository 패턴을 따름
 */
public interface TimeSlotRepository extends TimeSlotRepositoryCustom {

  TimeSlot save(TimeSlot timeSlot);

  Optional<TimeSlot> findById(Long id);

  void delete(TimeSlot timeSlot);

  void deleteById(Long id);

  List<TimeSlot> findAll();

  boolean existsById(Long id);

  List<TimeSlot> findByBoothId(Long boothId);

  List<TimeSlot> findByBoothIdAndStatus(Long boothId, TimeSlotStatus status);

  Optional<TimeSlot> findByIdAndBoothId(Long id, Long boothId);
}
