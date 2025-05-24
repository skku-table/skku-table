package com.skkutable.service;

import com.skkutable.domain.Role;
import com.skkutable.domain.User;
import com.skkutable.dto.UserDto;
import com.skkutable.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User join(UserDto dto, String adminSecret) {
    if (dto.getRole() == Role.ADMIN) {
      if (adminSecret == null || !adminSecret.equals("skku2023")) {
        throw new IllegalArgumentException("관리자 권한을 부여하기 위한 어드민 시크릿과 일치하지 않습니다.");
      }
    }
    validateDuplicateUser(dto.getEmail());

    return userRepository.save(
        new User(
        dto.getName(),
        dto.getEmail(),
        passwordEncoder.encode(dto.getPassword()),
        dto.getRole()));
  }

  private void validateDuplicateUser(String email) {
    userRepository.findByEmail(email)
        .ifPresent(m -> {
          throw new IllegalStateException("이미 존재하는 회원입니다.");
        });
  }


  public List<User> findUsers() { return userRepository.findAll();}

  public Optional<User> findOne(Long userId) {
      return userRepository.findById(userId);
  }
  public Optional<User> findOne(String email) {return userRepository.findByEmail(email);}

}
