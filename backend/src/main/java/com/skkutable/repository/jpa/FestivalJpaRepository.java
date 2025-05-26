package com.skkutable.repository.jpa;

import com.skkutable.domain.Festival;
import com.skkutable.repository.FestivalRepository;
import com.skkutable.repository.custom.FestivalRepositoryCustom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalJpaRepository extends JpaRepository<Festival, Long>,
    FestivalRepository, FestivalRepositoryCustom {

  @Override
  Optional<Festival> findByName(String name);
}
