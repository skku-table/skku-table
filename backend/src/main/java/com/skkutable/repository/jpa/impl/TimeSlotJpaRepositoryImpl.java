package com.skkutable.repository.jpa.impl;

import com.skkutable.domain.TimeSlot;
import com.skkutable.repository.custom.TimeSlotRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TimeSlotJpaRepositoryImpl implements TimeSlotRepositoryCustom {

  @PersistenceContext
  private EntityManager em;

  @Override
  public Optional<TimeSlot> findByIdWithLock(Long id) {
    String query = "SELECT ts FROM TimeSlot ts WHERE ts.id = :id";
    List<TimeSlot> results = em.createQuery(query, TimeSlot.class)
        .setParameter("id", id)
        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
        .getResultList();

    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @Override
  public Optional<TimeSlot> findByIdAndBoothIdWithLock(Long id, Long boothId) {
    String query = "SELECT ts FROM TimeSlot ts WHERE ts.id = :id AND ts.booth.id = :boothId";
    List<TimeSlot> results = em.createQuery(query, TimeSlot.class)
        .setParameter("id", id)
        .setParameter("boothId", boothId)
        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
        .getResultList();

    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @Override
  public List<TimeSlot> findByBoothIdAndTimeBetween(Long boothId, LocalDateTime startTime,
      LocalDateTime endTime) {
    String query = "SELECT ts FROM TimeSlot ts WHERE ts.booth.id = :boothId " +
        "AND ((ts.startTime <= :endTime AND ts.endTime >= :startTime) OR " +
        "(ts.startTime >= :startTime AND ts.startTime <= :endTime) OR " +
        "(ts.endTime >= :startTime AND ts.endTime <= :endTime))";

    return em.createQuery(query, TimeSlot.class)
        .setParameter("boothId", boothId)
        .setParameter("startTime", startTime)
        .setParameter("endTime", endTime)
        .getResultList();
  }

  @Override
  public List<TimeSlot> findAvailableTimeSlotsByBoothId(Long boothId) {
    String query = "SELECT ts FROM TimeSlot ts WHERE ts.booth.id = :boothId " +
        "AND ts.status = 'AVAILABLE' AND ts.currentCapacity < ts.maxCapacity";

    return em.createQuery(query, TimeSlot.class)
        .setParameter("boothId", boothId)
        .getResultList();
  }

  @Override
  public boolean existsByBoothIdAndStartTimeAndEndTime(Long boothId, LocalDateTime startTime,
      LocalDateTime endTime) {
    String query = "SELECT COUNT(ts) FROM TimeSlot ts WHERE ts.booth.id = :boothId " +
        "AND ts.startTime = :startTime AND ts.endTime = :endTime";

    Long count = em.createQuery(query, Long.class)
        .setParameter("boothId", boothId)
        .setParameter("startTime", startTime)
        .setParameter("endTime", endTime)
        .getSingleResult();

    return count > 0;
  }
} 