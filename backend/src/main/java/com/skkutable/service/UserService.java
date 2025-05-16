package com.skkutable.service;

import com.skkutable.domain.User;
import com.skkutable.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User join(User user) {
    validateDuplicateUser(user);
    return userRepository.save(user);
  }

  private void validateDuplicateUser(User user) {
    userRepository.findByName(user.getName())
        .ifPresent(m -> {
          throw new IllegalStateException("이미 존재하는 회원입니다.");
        });
  }


  public List<User> findUsers() { return userRepository.findAll();}

  public Optional<User> findOne(Long userId) {
      return userRepository.findById(userId);
  }

}
