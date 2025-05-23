package com.skkutable.repository;

import com.skkutable.domain.Booth;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaBoothRepository extends JpaRepository<Booth, Long>, BoothRepository {

  @Override
  Optional<Booth> findByName(String name);

  @Override
  Optional<Booth> findByIdAndFestivalId(Long id, Long festivalId);

  @Override
  List<Booth> findByFestivalId(Long festivalId);
}
