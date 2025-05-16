package com.skkutable.repository;

import com.skkutable.domain.Festival;
import java.util.List;
import java.util.Optional;

public interface FestivalRepository {
  Festival save(Festival festival);
  Optional<Festival> findById(Long id);
  void delete(Festival festival);
  void deleteById(Long id);
  Optional<Festival> findByName(String name);
  List<Festival> findAll();
}
