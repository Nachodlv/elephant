package com.lab.elephant.service;

import com.lab.elephant.model.User;
import com.lab.elephant.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  
  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
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
  public Optional<User> updatePassword(User user, String newPassword) {
    final Optional<User> optionalUser = getUser(user.getUuid());
    if (optionalUser.isPresent()) {
      user.setPassword(passwordEncoder.encode(newPassword));
      return Optional.of(userRepository.save(user));
    }
    return Optional.empty();
  }
}