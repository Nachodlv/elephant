package com.lab.elephant.controller;

import com.lab.elephant.model.*;
import com.lab.elephant.service.EmailService;
import com.lab.elephant.service.PermissionService;
import com.lab.elephant.service.TokenService;
import com.lab.elephant.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(path = "/user")
public class UserController {
  private final UserService userService;
  private final TokenService tokenService;
  private final BCryptPasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final PermissionService permissionService;
  
  public UserController(UserService userService, TokenService tokenService, BCryptPasswordEncoder passwordEncoder, EmailService emailService, PermissionService permissionService) {
    this.userService = userService;
    this.tokenService = tokenService;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
    this.permissionService = permissionService;
  }

  @PostMapping(path = "/create")
  public void createUser(@RequestBody @Valid User user) {
    final Optional<User> userByEmail = userService.getByEmail(user.getEmail());
    if (userByEmail.isPresent())
      throw new ResponseStatusException(
              HttpStatus.CONFLICT, "Email already in use");

    userService.addUser(user);
    emailService.sendWelcomeEmail(user);
  }

  @GetMapping()
  public User getUser() {
    return getAuthenticatedUser();
  }

  @PutMapping("/updatePassword")
  public void updatePassword(@Valid @RequestBody UpdatePasswordDto dto) {
    final User user = getAuthenticatedUser();
    if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword()))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect Password");
    if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword()))
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different from old one");
    userService.updatePassword(user.getEmail(), dto.getNewPassword());
    emailService.sendUpdatedPasswordEmail(user);
  }

  @PutMapping("/editUser")
  public void editUser(@Valid @RequestBody EditUserDTO dto) {
    final User user = getAuthenticatedUser();
    userService.editUser(user.getEmail(), dto);
  }

  @GetMapping("/notes")
  public List<NoteDTO> getAllNotesByUser() {
    final User user = getAuthenticatedUser();
    final List<Note> allNotesByUser = userService.getAllNotesByUser(user);
    final List<NoteDTO> noteDTOS = new ArrayList<>();
    
    allNotesByUser.forEach(n -> {
      //no need to check if permission exists because getAllNoteByUser gets the notes with the permissions.
      final boolean isPinned = permissionService.getPermissionBetween(user, n).get().isPinned();
      noteDTOS.add(new NoteDTO(n, isPinned));
    });
    return noteDTOS;
  }

  @PutMapping()
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
