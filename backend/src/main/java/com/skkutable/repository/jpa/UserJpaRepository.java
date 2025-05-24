package com.skkutable.repository.jpa;

import com.skkutable.domain.User;
import com.skkutable.repository.UserRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long>, UserRepository {

  @Override
  Optional<User> findByName(String name);

  @Override
  Optional<User> findByEmail(String email);
}
