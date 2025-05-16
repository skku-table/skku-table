package com.skkutable.repository;

import com.skkutable.domain.Booth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaBoothRepository extends JpaRepository<Booth, Long>, BoothRepository {

  @Override
  Optional<Booth> findByName(String name);
}
