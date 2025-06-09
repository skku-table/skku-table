package com.skkutable.service;

import com.skkutable.domain.Role;
import com.skkutable.domain.User;
import com.skkutable.dto.UserDto;
import com.skkutable.exception.BadRequestException;
import com.skkutable.exception.ConflictException;
import com.skkutable.exception.ResourceNotFoundException;
import com.skkutable.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
        throw new BadRequestException("관리자 권한을 부여하기 위한 어드민 시크릿과 일치하지 않습니다.");
      }
    }
    validateDuplicateUser(dto.getEmail());

    return userRepository.save(
        new User(dto.getName(), dto.getEmail(), passwordEncoder.encode(dto.getPassword()),
            dto.getRole()));
  }

  private void validateDuplicateUser(String email) {
    userRepository.findByEmail(email).ifPresent(m -> {
      throw new ConflictException("이미 존재하는 회원입니다. : " + email);
    });
  }

  @Autowired
  private CloudinaryService cloudinaryService;

  public String updateProfileImage(String email, MultipartFile imageFile) {
    User user = findOne(email);

    // 기존 이미지 삭제
    if (user.getProfileImageUrl() != null) {
      String publicId = extractPublicIdFromUrl(user.getProfileImageUrl());
      cloudinaryService.deleteImage(publicId);
    }

    String newImageUrl = cloudinaryService.uploadImage(imageFile);
    user.setProfileImageUrl(newImageUrl);
    return newImageUrl;
  }

  public void deleteProfileImage(String email) {
    User user = findOne(email);
    if (user.getProfileImageUrl() != null) {
      String publicId = extractPublicIdFromUrl(user.getProfileImageUrl());
      cloudinaryService.deleteImage(publicId);
      user.setProfileImageUrl(null);
    }
  }

  private String extractPublicIdFromUrl(String imageUrl) {
    String[] parts = imageUrl.split("/");
    String filename = parts[parts.length - 1];
    String publicId = filename.substring(0, filename.lastIndexOf("."));
    String folder = parts[parts.length - 2];
    return folder + "/" + publicId;
  }


  public List<User> findUsers() {
    return userRepository.findAll();
  }

  public User findOne(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
  }

  public User findOne(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
  }

  public User getCurrentUser(String email) {
    return findOne(email);
  }

}
