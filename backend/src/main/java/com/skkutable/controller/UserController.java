package com.skkutable.controller;

import com.skkutable.domain.User;
import com.skkutable.dto.HostContentResponseDto;
import com.skkutable.dto.UserDto;
import com.skkutable.service.HostContentService;
import com.skkutable.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {

  private final String REDACTED = "[REDACTED]";
  private final UserService userService;
  private final HostContentService hostContentService;

  @Autowired
  public UserController(UserService userService, HostContentService hostContentService) {
    this.userService = userService;
    this.hostContentService = hostContentService;
  }

  @GetMapping
  public List<UserDto> getUsers() {
    return userService.findUsers().stream()
        .map(u -> new UserDto(
            u.getId(),
            u.getName(),
            u.getEmail(),
            REDACTED,
            u.getRole(),
            u.getUniversity(),
            u.getMajor(),
            u.getProfileImageUrl()
        ))
        .toList();
  }

  @PostMapping("/signup")
  @ResponseStatus(HttpStatus.CREATED)
  public UserDto addUser(@RequestBody @Valid UserDto dto,
      @RequestHeader(value = "X-ADMIN-SECRET", required = false) String adminSecret) {
    var createUser = userService.join(dto, adminSecret);
    return new UserDto(
        createUser.getId(),
        createUser.getName(),
        createUser.getEmail(),
        REDACTED,
        createUser.getRole(),
        createUser.getUniversity(),
        createUser.getMajor(),
        createUser.getProfileImageUrl()
    );
  }

  @PutMapping("/me/profile-image")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<String> updateProfileImage(
      @AuthenticationPrincipal(expression = "username") String email,
      @RequestParam("image") MultipartFile imageFile
  ) {
    String imageUrl = userService.updateProfileImage(email, imageFile);
    return ResponseEntity.ok(imageUrl);
  }

  @DeleteMapping("/me/profile-image")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> deleteProfileImage(
      @AuthenticationPrincipal(expression = "username") String email
  ) {
    userService.deleteProfileImage(email);
    return ResponseEntity.noContent().build();
  }


  /* 로그인은 Spring Security 필터가 처리 (POST /users/login) */

  /* 세션 연장 - 단순 ‘ping’ */
  @GetMapping("/session/refresh")
  public ResponseEntity<Void> refreshSession(HttpSession session) {
    session.setMaxInactiveInterval(30 * 60); // 30분으로 재설정 (예시)
    return ResponseEntity.ok().build();
  }

  @GetMapping("/me")
  public UserDto me(@AuthenticationPrincipal(expression = "username") String email) {
    User user = userService.findOne(email);
    return new UserDto(
        user.getId(),
        user.getName(),
        user.getEmail(),
        REDACTED,
        user.getRole(),
        user.getUniversity(),
        user.getMajor(),
        user.getProfileImageUrl()
    );
  }

  @GetMapping("{id}")
  public UserDto getUserById(@PathVariable("id") Long userId) {
    User user = userService.findOne(userId);
    return new UserDto(
        user.getId(),
        user.getName(),
        user.getEmail(),
        REDACTED,
        user.getRole(),
        user.getUniversity(),
        user.getMajor(),
        user.getProfileImageUrl()
    );
  }

  /* 호스트가 생성한 부스 조회 */
  @GetMapping("/me/booths")
  @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
  public ResponseEntity<HostContentResponseDto> getHostContent(
      @AuthenticationPrincipal(expression = "username") String email) {
    HostContentResponseDto response = hostContentService.getHostContent(email);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UserDto> updateMe(
      @AuthenticationPrincipal(expression = "username") String email,
      @RequestBody Map<String, Object> updates
  ) {
    User updatedUser = userService.updatePartial(email, updates);
    return ResponseEntity.ok(new UserDto(
        updatedUser.getId(),
        updatedUser.getName(),
        updatedUser.getEmail(),
        "[REDACTED]",
        updatedUser.getRole(),
        updatedUser.getUniversity(),
        updatedUser.getMajor(),
        updatedUser.getProfileImageUrl()
    ));
  }
}
