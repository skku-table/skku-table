package com.skkutable.repository.jpa.impl;

import com.skkutable.domain.Reservation;
import com.skkutable.repository.custom.ReservationRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class ReservationJpaRepositoryImpl implements ReservationRepositoryCustom {
  @PersistenceContext
  private EntityManager em;

  @Override
  public List<Reservation> findByUserIdWithBoothAndFestival(Long userId) {
    String query = "SELECT r FROM Reservation r " +
        "JOIN FETCH r.booth b " +
        "JOIN FETCH b.festival " +
        "WHERE r.user.id = :userId";
    return em.createQuery(query, Reservation.class)
        .setParameter("userId", userId)
        .getResultList();
  }

}
