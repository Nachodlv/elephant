package com.lab.elephant.service;

import com.lab.elephant.model.User;
import com.lab.elephant.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
  
  private final UserRepository userRepository;
  
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
  
  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }
  
  @Override
  public User addUser(User user) {
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
}