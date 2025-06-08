package com.skkutable.repository;

import com.skkutable.domain.Festival;
import com.skkutable.repository.custom.FestivalRepositoryCustom;
import java.util.List;
import java.util.Optional;

public interface FestivalRepository extends FestivalRepositoryCustom {

  Festival save(Festival festival);

  Optional<Festival> findById(Long id);

  void delete(Festival festival);

  void deleteById(Long id);

  Optional<Festival> findByName(String name);

  List<Festival> findAll();

  boolean existsByName(String name);

  boolean existsById(Long festivalId);
}
