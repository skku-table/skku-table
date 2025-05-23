package com.skkutable.repository.jpa;

import com.skkutable.domain.Booth;
import com.skkutable.repository.BoothRepository;
import com.skkutable.repository.custom.BoothRepositoryCustom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoothJpaRepository extends JpaRepository<Booth, Long>, BoothRepository,
    BoothRepositoryCustom {

  @Override
  Optional<Booth> findByName(String name);

  @Override
  Optional<Booth> findByIdAndFestivalId(Long id, Long festivalId);

  @Override
  List<Booth> findByFestivalId(Long festivalId);
}
