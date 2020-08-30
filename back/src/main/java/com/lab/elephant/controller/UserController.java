package com.lab.elephant.controller;

import com.lab.elephant.model.User;
import com.lab.elephant.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(path = "/user")
public class UserController {
  private final UserService userService;
  
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping(path = "/create")
  public void createUser(@RequestBody @Valid User user) {
    final Optional<User> userByEmail = userService.getByEmail(user.getEmail());
    if (userByEmail.isPresent())
      throw new ResponseStatusException(
              HttpStatus.CONFLICT, "Email already in use");
    
    userService.addUser(user);
  }
}
