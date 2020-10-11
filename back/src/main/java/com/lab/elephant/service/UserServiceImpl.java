package com.lab.elephant.service;

import com.lab.elephant.model.EditUserDTO;
import com.lab.elephant.model.Note;
import com.lab.elephant.model.Permission;
import com.lab.elephant.model.User;
import com.lab.elephant.repository.PermissionRepository;
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
  private final PermissionRepository permissionRepository;

  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, PermissionRepository permissionRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.permissionRepository = permissionRepository;
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
    List<Permission> permissions = permissionRepository.findAllByUser(user);
    List<Note> notes = new ArrayList<>();
    permissions.forEach(permission -> notes.add(permission.getNote()));
    return notes;
  }
}