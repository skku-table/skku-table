package com.skkutable.repository;

import com.skkutable.domain.Festival;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaFestivalRepository extends JpaRepository<Festival, Long>, FestivalRepository {

  @Override
  Optional<Festival> findByName(String name);
}
