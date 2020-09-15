package com.lab.elephant.service;

import com.lab.elephant.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
  List<User> getAllUsers();

  User addUser(User user);

  Optional<User> getUser(long id);
  
  Optional<User> getByEmail(String email);
  
  Optional<User> updatePassword(User user, String newPassword);
}
