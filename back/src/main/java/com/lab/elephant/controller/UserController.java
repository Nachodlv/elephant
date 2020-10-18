package com.lab.elephant.controller;

import com.lab.elephant.model.*;
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
import java.util.List;
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
    final User user = getAuthenticatedUser();
    if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword()))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect Password");
    userService.updatePassword(user.getEmail(), dto.getNewPassword());
  }

  @PutMapping("/editUser")
  public void editUser(@Valid @RequestBody EditUserDTO dto) {
    final User user = getAuthenticatedUser();
    userService.editUser(user.getEmail(), dto);
  }

  @GetMapping("/notes")
  public List<Note> getAllNotesByUser() {
    final String mail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optionalUser = userService.getByEmail(mail);
    if (optionalUser.isPresent()) {
      final User user = optionalUser.get();
      return userService.getAllNotesByUser(user);
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
  }

  @DeleteMapping()
  public void deleteUser(@RequestBody DeleteUserDTO dto) {
    final String password = dto.getPassword();
    final User user = getAuthenticatedUser();
    if (!passwordEncoder.matches(password, user.getPassword()))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect Password");
    userService.delete(user.getUuid());
  }
  
  private User getAuthenticatedUser() {
    final String mail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optionalUser = userService.getByEmail(mail);
    //this .get() is not checked because we know by the JWTFilter that the user exists.
    return optionalUser.get();
  }
}
