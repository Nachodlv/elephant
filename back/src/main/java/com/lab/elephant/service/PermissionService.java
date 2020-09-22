package com.lab.elephant.service;

import com.lab.elephant.model.Note;
import com.lab.elephant.model.PermissionType;
import com.lab.elephant.model.User;
import org.springframework.stereotype.Service;

@Service
public interface PermissionService {
  
  void addRelationship(User user, Note note, PermissionType type);
}
