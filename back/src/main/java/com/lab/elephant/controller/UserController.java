package com.lab.elephant.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lab.elephant.model.User;
import com.lab.elephant.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Base64;
import java.util.Optional;

import static com.lab.elephant.security.SecurityConstants.HEADER_STRING;
import static com.lab.elephant.security.SecurityConstants.TOKEN_PREFIX;

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

    String token = request.getHeader(HEADER_STRING);

    if (token != null) {
      final DecodedJWT decode = JWT.decode(token.replace(TOKEN_PREFIX, ""));
      String payload = decode.getPayload();

      String[] body = new String(Base64.getDecoder().decode(payload)).split("\"");
      String email = body[3];

      Optional<User> optionalUser = userService.getByEmail(email);
      if (optionalUser.isPresent()) {
        return optionalUser.get();
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
      }
    }

    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token Not Found");
  }

}
