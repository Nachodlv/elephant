package com.lab.elephant.service;

import com.lab.elephant.model.Note;
import com.lab.elephant.model.Permission;
import com.lab.elephant.model.PermissionType;
import com.lab.elephant.model.User;
import com.lab.elephant.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionServiceImpl implements PermissionService {

  private final PermissionRepository permissionRepository;

  public PermissionServiceImpl(PermissionRepository permissionRepository) {
    this.permissionRepository = permissionRepository;
  }

  @Override
  public void addRelationship(User user, Note note, PermissionType type) {
    Permission p = new Permission(user, note, type);
    List<Permission> userPermissions = user.getPermissions();
    List<Permission> notePermissions = note.getPermissions();
    userPermissions.add(p);
    notePermissions.add(p);
    user.setPermissions(userPermissions);
    note.setPermissions(notePermissions);
    permissionRepository.save(p);
  }

  @Override
  public Optional<PermissionType> getPermissionTypeBetween(User user, Note note) {
    final List<Permission> permissions = note.getPermissions();
    for (Permission p : permissions) {
      if (user.getUuid() == p.getUser().getUuid())
        return Optional.of(p.getType());
    }
    return Optional.empty();
  }

  @Override
  public List<Permission> findAllByUser(User user) {
    return permissionRepository.findAllByUser(user);
  }

  @Override
  public void editRelationship(User user, Note note, String newPermissionType) {
    final Optional<Permission> optionalPermission = getPermissionBetween(user, note);
    if (!optionalPermission.isPresent()) return;
    Permission p = optionalPermission.get();
    if (newPermissionType.equals("delete"))
      permissionRepository.delete(p);
    else {
      p.setType(PermissionType.valueOf(newPermissionType));
      permissionRepository.save(p);
    }
  }

  @Override
  public Optional<Permission> getPermissionBetween(User user, Note note) {
    return permissionRepository.findByUserAndNote(user, note);
  }

  @Override
  public boolean deletePermission(Note note, User user) {
    final Optional<Permission> optionalPermission = getPermissionBetween(user, note);
    if (!optionalPermission.isPresent())
      return false;
    Permission p = optionalPermission.get();
    permissionRepository.delete(p);
    return true;
  }
}
