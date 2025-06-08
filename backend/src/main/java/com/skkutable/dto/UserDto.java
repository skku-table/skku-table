package com.skkutable.dto;

import com.skkutable.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserDto {

  private final Long id;

  @NotBlank(message = "이름을 입력해야 합니다.")
  @Pattern(regexp = "^[가-힣A-Za-z0-9]+$", message = "보관함 이름에는 한글, 영문, 숫자만 가능합니다")
  @Size(min = 1, max = 5, message = "이름은 한글자 이상, 다섯글자 이하입니다.")
  private final String name;

  @NotBlank
  @Email
  private final String email;

  @NotBlank
  @Size(min = 6)
  private final String password;

  private final Role role;


  public UserDto(Long id, String name, String email, String password, Role role) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = role == null ? Role.USER : role;
  }
}
