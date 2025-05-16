package com.skkutable.repository;

import com.skkutable.domain.Booth;
import java.util.List;
import java.util.Optional;

public interface BoothRepository {
  Booth save(Booth booth);
  Optional<Booth> findById(Long id);
  void delete(Booth booth);
  void deleteById(Long id);
  Optional<Booth> findByName(String name);
  Optional<Booth> findByIdAndFestivalId(Long id, Long festivalId);
  List<Booth> findAll();
  List<Booth> findByFestivalId(Long festivalId);
}
