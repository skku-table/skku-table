package com.skkutable.repository.jpa.impl;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.repository.custom.BoothRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

public class BoothJpaRepositoryImpl implements BoothRepositoryCustom {
  @PersistenceContext
  private EntityManager em;

  @Override
  @Transactional
  public Booth createBooth(Long festivalId, Booth booth) {
    // ① 영속성 컨텍스트 안에서 proxy reference 획득
    Festival festivalRef = em.find(Festival.class, festivalId);
    if (festivalRef == null) throw new ResourceNotFoundException("Festival not found: " + festivalId);
    // ② 양방향 동기화
    festivalRef.addBooth(booth);   // festival.booths 에도 추가
    return booth;
  }
}
