package com.skkutable.repository.jpa;

import com.skkutable.domain.TimeSlot;
import com.skkutable.domain.TimeSlotStatus;
import com.skkutable.repository.TimeSlotRepository;
import com.skkutable.repository.custom.TimeSlotRepositoryCustom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSlotJpaRepository extends JpaRepository<TimeSlot, Long>, TimeSlotRepository,
    TimeSlotRepositoryCustom {

  @Override
  List<TimeSlot> findByBoothId(Long boothId);

  @Override
  List<TimeSlot> findByBoothIdAndStatus(Long boothId, TimeSlotStatus status);

  @Override
  Optional<TimeSlot> findByIdAndBoothId(Long id, Long boothId);
}
