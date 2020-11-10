package com.lab.elephant.service;

import com.lab.elephant.model.Note;
import com.lab.elephant.model.Permission;
import com.lab.elephant.model.PermissionType;
import com.lab.elephant.model.User;
import com.lab.elephant.repository.PermissionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class PermissionServiceTest {
  @TestConfiguration
  static class PermissionServiceTestContextConfiguration {
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Bean
    public PermissionService permissionService() {
      return new PermissionServiceImpl(permissionRepository);
    }
  }
  
  @Autowired
  private PermissionService permissionService;
  @MockBean
  private PermissionRepository permissionRepository;
  
  @Test
  public void addRelationshipTest() {
    final User user = new User();
    final Note note = new Note();
    final Permission p = new Permission(user, note, PermissionType.Viewer);
    when(permissionRepository.save(p)).thenReturn(p);
    permissionService.addRelationship(user, note, PermissionType.Viewer);
    
    assertThat(user.getPermissions().size()).isEqualTo(1);
    assertThat(note.getPermissions().size()).isEqualTo(1);
    assertThat(note.getPermissions().get(0)).isEqualTo(user.getPermissions().get(0));
    assertThat(user.getPermissions().get(0).getType()).isEqualTo(PermissionType.Viewer);
  }
  
  @Test
  public void getPermissionTypeBetween_WhenPermissionExists_ShouldReturnOptionalOfIt() {
    final User user = new User();
    final Note note = new Note();
    final Permission p = new Permission(user, note, PermissionType.Editor);
    final List<Permission> permissionList = new ArrayList<>();
    permissionList.add(p);
    user.setPermissions(permissionList);
    note.setPermissions(permissionList);
    
    final Optional<PermissionType> permissionBetween = permissionService.getPermissionTypeBetween(user, note);
    assertThat(permissionBetween.isPresent()).isTrue();
    assertThat(permissionBetween.get()).isEqualTo(p.getType());
  }
  
  @Test
  public void getPermissionTypeBetween_WhenPermissionDoesNotExist_ShouldReturnEmptyOptional() {
    final Optional<PermissionType> permissionBetween = permissionService.getPermissionTypeBetween(new User(), new Note());
    assertThat(permissionBetween.isPresent()).isFalse();
  }
  
  @Test
  public void findAllByUser_ShouldCallThePermissionRepositoryToDoIt() {
    final User user = new User();
    permissionService.findAllByUser(user);
    Mockito.verify(permissionRepository, Mockito.times(1)).findAllByUser(user);
  }
  
  @Test
  public void getPermissionBetween_ShouldCallThePermissionRepositoryToDoIt() {
    final User user = new User();
    final Note note = new Note();
    permissionService.getPermissionBetween(user, note);
    Mockito.verify(permissionRepository, Mockito.times(1)).findByUserAndNote(user, note);
  }
  
  @Test
  public void editRelationship_WhenNewPermissionTypeIsValidAndNotDeleted_ShouldSaveEditedRelationShip() {
    final User user = new User();
    final Note note = new Note();
    final Permission p = new Permission(user, note, PermissionType.Editor);
    final String newPermissionType = "Viewer";
    when(permissionRepository.findByUserAndNote(user, note)).thenReturn(Optional.of(p));
    
    permissionService.editRelationship(user, note, newPermissionType);
    assertThat(p.getType()).isEqualTo(PermissionType.valueOf(newPermissionType));
    Mockito.verify(permissionRepository, Mockito.times(1)).save(p);
  }
  
  @Test
  public void editRelationship_WhenNewPermissionTypeIsDeleted_ShouldDeleteTheRelationShip() {
    final User user = new User();
    final Note note = new Note();
    final Permission p = new Permission(user, note, PermissionType.Editor);
    final String newPermissionType = "delete";
    when(permissionRepository.findByUserAndNote(user, note)).thenReturn(Optional.of(p));
    
    permissionService.editRelationship(user, note, newPermissionType);
    Mockito.verify(permissionRepository, Mockito.times(1)).delete(p);
  }
  
  @Test
  public void deleteNote_WhenUserIsCollaborator_ShouldDeletePermission() {
    final User user = new User();
    final Note note = new Note();
    final Permission p = new Permission(user, note, PermissionType.Viewer);
    when(permissionRepository.save(p)).thenReturn(p);
    permissionService.addRelationship(user, note, PermissionType.Viewer);
    
    when(permissionRepository.findByUserAndNote(user, note)).thenReturn(Optional.of(p));
    Optional<Permission> permission = permissionService.getPermissionBetween(user, note);
    assertThat(permission.isPresent()).isTrue();
    
    boolean perm = permissionService.deletePermission(note, user);
    assertThat(perm).isTrue();
  
    when(permissionRepository.findByUserAndNote(user, note)).thenReturn(Optional.empty());
    boolean perm2 = permissionService.deletePermission(note, user);
    assertThat(perm2).isFalse();
    Optional<Permission> permissionThen = permissionService.getPermissionBetween(user, note);
    assertThat(permissionThen.isPresent()).isFalse();
  }
  
  @Test
  public void changePin_WhenPermissionBetweenUserAndNoteDoesNotExist_ShouldReturnFalse() {
    final boolean b = permissionService.changePin(new User(), new Note());
    assertThat(b).isFalse();
  }
  
  @Test
  public void changePin_WhenPermissionBetweenUserAndNoteExists_ShouldChangePinAndSave() {
    final User user = new User();
    final Note note = new Note();
    final Permission permission = new Permission(user, note, PermissionType.Owner);
    
    Mockito.when(permissionRepository.findByUserAndNote(user, note)).thenReturn(Optional.of(permission));
    final boolean b1 = permissionService.changePin(user, note);
    final boolean b2 = permissionService.changePin(user, note);
    final boolean b3 = permissionService.changePin(user, note);
    Mockito.verify(permissionRepository, Mockito.times(3)).save(permission);
    
    assertThat(b1).isTrue();
    assertThat(b2).isFalse();
    assertThat(b3).isTrue();
  }
}
