package com.lab.elephant.controller;

import com.lab.elephant.model.EditUserDTO;
import com.lab.elephant.model.UpdatePasswordDto;
import com.lab.elephant.model.User;
import com.lab.elephant.service.EmailService;
import com.lab.elephant.service.TokenService;
import com.lab.elephant.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
  private final TokenService tokenService;
  private final BCryptPasswordEncoder passwordEncoder;
  private final EmailService emailService;
  public UserController(UserService userService, TokenService tokenService, BCryptPasswordEncoder passwordEncoder, EmailService emailService) {
    this.userService = userService;
    this.tokenService = tokenService;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
  }

  @PostMapping(path = "/create")
  public void createUser(@RequestBody @Valid User user) {
    final Optional<User> userByEmail = userService.getByEmail(user.getEmail());
    if (userByEmail.isPresent())
      throw new ResponseStatusException(
              HttpStatus.CONFLICT, "Email already in use");

    userService.addUser(user);
    emailService.sendSimpleEmail(user);
  }

  @GetMapping()
  public User getUser(HttpServletRequest request) {

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

  @PutMapping("/updatePassword")
  public void updatePassword(@Valid @RequestBody UpdatePasswordDto dto) {
    final String string = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    final User user = userService.getByEmail(string).get();
    
    if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword()))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect Password");
    userService.updatePassword(user.getEmail(), dto.getNewPassword());
  }
  
  @PutMapping("/editUser")
  public void editUser(@Valid @RequestBody EditUserDTO dto) {
    final String string = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    final User user = userService.getByEmail(string).get();
    
    userService.editUser(user.getEmail(), dto);
  }
}
