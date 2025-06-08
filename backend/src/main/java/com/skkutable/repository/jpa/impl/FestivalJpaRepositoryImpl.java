package com.skkutable.repository.jpa.impl;

import com.skkutable.domain.Festival;
import com.skkutable.repository.custom.FestivalRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class FestivalJpaRepositoryImpl implements FestivalRepositoryCustom {

  @PersistenceContext
  private EntityManager em;

  @Override
  public List<Festival> searchDynamic(String keyword) {
    if (keyword == null || keyword.trim().isBlank() || keyword.isEmpty()) {
      return List.of(); // 빈 문자열이나 null인 경우 빈 리스트 반환
    }

    String trimmedKeyword = keyword.trim();
    String query = "SELECT f FROM Festival f " +
        "WHERE lower(f.name) LIKE lower(:keyword) " +
        "OR lower(f.location) LIKE lower(:keyword) " +
        "OR lower(f.description) LIKE lower(:keyword)";

    return em.createQuery(query, Festival.class)
        .setParameter("keyword", "%" + trimmedKeyword + "%")
        .getResultList();
  }
}
