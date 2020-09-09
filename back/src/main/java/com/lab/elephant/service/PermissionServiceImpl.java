package com.lab.elephant.service;

import com.lab.elephant.model.Note;
import com.lab.elephant.model.Permission;
import com.lab.elephant.model.PermissionType;
import com.lab.elephant.model.User;
import com.lab.elephant.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
  
  private final PermissionRepository permissionRepository;
  
  public PermissionServiceImpl(PermissionRepository permissionRepository) {
    this.permissionRepository = permissionRepository;
  }
  
  @Override
  public void addRelationShip(User user, Note note, PermissionType type) {
    //todo que pasa si ya hay una relationship???
    Permission p = new Permission(user, note, type);
    List<Permission> userPermissions = user.getPermissions();
    List<Permission> notePermissions = note.getPermissions();
    userPermissions.add(p);
    notePermissions.add(p);
    user.setPermissions(userPermissions);
    note.setPermissions(notePermissions);
    permissionRepository.save(p);
  }
}
