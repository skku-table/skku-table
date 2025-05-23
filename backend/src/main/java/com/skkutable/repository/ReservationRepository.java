package com.skkutable.repository;

import com.skkutable.domain.Reservation;
import com.skkutable.repository.custom.ReservationRepositoryCustom;
import java.util.Optional;

import java.util.List;

public interface ReservationRepository extends ReservationRepositoryCustom {
    // 기본 CRUD
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(Long id);
    void deleteById(Long id);

    // 파생(derived) 쿼리 – 이름 파싱으로 자동 구현
    List<Reservation> findByBoothFestivalIdAndBoothId(Long festivalId, Long boothId);
}
