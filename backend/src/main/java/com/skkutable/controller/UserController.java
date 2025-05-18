package com.skkutable.controller;

import com.skkutable.domain.User;
import com.skkutable.dto.UserDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.skkutable.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  public List<User> getUsers() {
    return userService.findUsers();
  }

  @PostMapping("/users/signup")
  public User addUser(@RequestBody UserDto userDto) {
    User user = new User(userDto.getName(), userDto.getEmail(), userDto.getPassword());
    return userService.join(user);
  }

}
