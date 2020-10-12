package com.lab.elephant.service;

import com.lab.elephant.model.EditUserDTO;
import com.lab.elephant.model.Note;
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
  
  Optional<User> updatePassword(String email, String newPassword);
  
  Optional<User> editUser(String email, EditUserDTO dto);

  List<Note> getAllNotesByUser(User user);
}
