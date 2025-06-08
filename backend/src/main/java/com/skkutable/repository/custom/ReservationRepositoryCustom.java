package com.skkutable.repository.custom;

import com.skkutable.domain.Reservation;
import java.util.List;

public interface ReservationRepositoryCustom {

  /**
   * Reservation + Booth + Festival 을 페치 조인으로 한 번에 가져온다
   */
  List<Reservation> findByUserIdWithBoothAndFestival(Long userId);
}
