package com.skkutable.repository.jpa;

import com.skkutable.domain.Reservation;
import com.skkutable.repository.ReservationRepository;
import com.skkutable.repository.custom.ReservationRepositoryCustom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long>,
    ReservationRepository, ReservationRepositoryCustom {

  @Override
  List<Reservation> findByBoothFestivalIdAndBoothId(Long festivalId, Long boothId);
}
