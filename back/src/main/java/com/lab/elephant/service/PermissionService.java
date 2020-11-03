package com.lab.elephant.service;

import com.lab.elephant.model.Note;
import com.lab.elephant.model.Permission;
import com.lab.elephant.model.PermissionType;
import com.lab.elephant.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PermissionService {
  
  void addRelationship(User user, Note note, PermissionType type);
  
  Optional<PermissionType> getPermissionTypeBetween(User user, Note note);
  
  List<Permission> findAllByUser(User user);
  
  void editRelationship(User user, Note note, String newPermissionType);
  
  Optional<Permission> getPermissionBetween(User user, Note note);
}
