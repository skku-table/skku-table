package com.skkutable.repository;

import com.skkutable.domain.Booth;
import com.skkutable.repository.custom.BoothRepositoryCustom;
import java.util.List;
import java.util.Optional;

public interface BoothRepository extends BoothRepositoryCustom {

  Booth save(Booth booth);

  Optional<Booth> findById(Long id);

  void delete(Booth booth);

  void deleteById(Long id);

  Optional<Booth> findByName(String name);

  Optional<Booth> findByIdAndFestivalId(Long id, Long festivalId);

  List<Booth> findAll();

  List<Booth> findByFestivalId(Long festivalId);

  boolean existsById(Long boothId);

  List<Booth> findByCreatedById(Long userId);

  List<Booth> findByFestivalIdAndCreatedById(Long festivalId, Long userId);
}
