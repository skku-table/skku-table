package com.skkutable.controller;

import com.skkutable.domain.User;
import com.skkutable.dto.UserDto;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import com.skkutable.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
  private final String REDACTED = "[REDACTED]";
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  // Admin용으로 모든 사용자 조회
  @GetMapping
  public List<UserDto> getUsers() {
    return userService.findUsers().stream().map(u -> new UserDto(u.getId(), u.getName(), u.getEmail(), REDACTED, u.getRole()))
        .toList();
  }

  @PostMapping("/signup")
  @ResponseStatus(HttpStatus.CREATED)
  public UserDto addUser(@RequestBody @Valid UserDto dto, @RequestHeader(value="X-ADMIN-SECRET", required = false) String adminSecret) {
    var createUser = userService.join(dto, adminSecret);
    return new UserDto(createUser.getId(), createUser.getName(), createUser.getEmail(), REDACTED, createUser.getRole());
  }

  /* 로그인은 Spring Security 필터가 처리 (POST /users/login) */

  /* 세션 연장 - 단순 ‘ping’ */
  @GetMapping("/session/refresh")
  public ResponseEntity<Void> refreshSession(HttpSession session) {
    session.setMaxInactiveInterval(30 * 60); // 30분으로 재설정 (예시)
    return ResponseEntity.ok().build();
  }

  /* 현재 로그인 사용자 정보 확인 */
  @GetMapping("/me")
  public UserDto me(@AuthenticationPrincipal(expression = "username") String email) {
    return userService.findOne(email)
        .map(u -> new UserDto(u.getId(), u.getName(), u.getEmail(), REDACTED, u.getRole()))
        .orElseThrow();
  }
}
