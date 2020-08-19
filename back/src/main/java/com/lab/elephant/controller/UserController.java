package com.lab.elephant.controller;

import com.lab.elephant.model.User;
import com.lab.elephant.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(path = "/user")
public class UserController {
  
  private final UserService userService;
  
  public UserController(UserService userService) {
    this.userService = userService;
  }
  
  @PostMapping(path = "/create")
  public String createUser(@RequestBody @Valid User user) {
    final Optional<User> userByEmail = userService.getByEmail(user.getEmail());
    if (userByEmail.isPresent())
      return "400";
    
    userService.addUser(user);
    return "200";
  }
  //todo ver bien que devolver.
}
