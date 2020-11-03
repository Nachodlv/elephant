package com.lab.elephant.service;

import com.lab.elephant.model.*;
import com.lab.elephant.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final PermissionService permissionService;
  private final NoteService noteService;
  
  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, PermissionService permissionService, NoteService noteService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.permissionService = permissionService;
    this.noteService = noteService;
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public User addUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  @Override
  public Optional<User> getUser(long id) {
    return userRepository.findById(id);
  }

  @Override
  public Optional<User> getByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public Optional<User> updatePassword(String email, String newPassword) {
    final Optional<User> optionalUser = getByEmail(email);
    if (optionalUser.isPresent()) {
      final User user = optionalUser.get();
      user.setPassword(passwordEncoder.encode(newPassword));
      return Optional.of(userRepository.save(user));
    }
    return Optional.empty();
  }

  @Override
  public Optional<User> editUser(String email, EditUserDTO dto) {
    final Optional<User> optionalUser = getByEmail(email);
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      user.setFirstName(dto.getFirstName());
      user.setLastName(dto.getLastName());
      return Optional.of(userRepository.save(user));
    }
    return optionalUser;

  }

  @Override
  public List<Note> getAllNotesByUser(User user) {
    List<Permission> permissions = permissionService.findAllByUser(user);
    List<Note> notes = new ArrayList<>();
    permissions.forEach(permission -> notes.add(permission.getNote()));
    return notes;
  }
  
  @Override
  public void delete(long id) {
    final Optional<User> optionalUser = getUser(id);
    if (optionalUser.isPresent()) {
      final User user = optionalUser.get();
      for (Note note : getAllNotesMadeByUser(user)) noteService.deleteNote(note.getUuid());
      userRepository.delete(user);
    }
  }
  
  @Override
  public List<Note> getAllNotesMadeByUser(User user) {
    List<Permission> permissions = permissionService.findAllByUser(user);
    List<Note> notes = new ArrayList<>();
    for (Permission p : permissions)
      if (p.getType().equals(PermissionType.Owner))
        notes.add(p.getNote());
    return notes;
  }
}
