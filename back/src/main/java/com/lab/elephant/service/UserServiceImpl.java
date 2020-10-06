package com.lab.elephant.service;

import com.lab.elephant.model.EditUserDTO;
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
}