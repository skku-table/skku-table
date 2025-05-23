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
    String query = "SELECT f FROM Festival f " +
        "WHERE lower(f.name) LIKE lower(:keyword) " +
        "OR lower(f.location) LIKE lower(:keyword) " +
        "OR lower(f.description) LIKE lower(:keyword)";

    return em.createQuery(query, Festival.class)
        .setParameter("keyword", "%" + keyword + "%")
        .getResultList();
  }
}
