package com.lab.elephant.controller;

import com.lab.elephant.model.User;
import com.lab.elephant.service.TokenService;
import com.lab.elephant.service.TokenServiceImpl;
import com.lab.elephant.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

import static com.lab.elephant.security.SecurityConstants.HEADER_STRING;

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

  @GetMapping(path = "/get")
  public User getUser(HttpServletRequest request) {
    // Have to instance it inside method, otherwise it is not functioning
    TokenService tokenService = new TokenServiceImpl();

    String token = request.getHeader(HEADER_STRING);

    if (token != null) {

      String email = tokenService.getEmailByToken(token);

      // verify and return if user exists with that email
      Optional<User> optionalUser = userService.getByEmail(email);
      if (optionalUser.isPresent()) {
        return optionalUser.get();
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
      }
    }

    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token Not Found");
  }

}
