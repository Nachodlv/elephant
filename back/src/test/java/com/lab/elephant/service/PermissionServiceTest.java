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

import static org.assertj.core.api.Assertions.assertThat;

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
    Mockito.when(permissionRepository.save(p)).thenReturn(p);
    permissionService.addRelationship(user, note, PermissionType.Viewer);
    
    assertThat(user.getPermissions().size()).isEqualTo(1);
    assertThat(note.getPermissions().size()).isEqualTo(1);
    assertThat(note.getPermissions().get(0)).isEqualTo(user.getPermissions().get(0));
    assertThat(user.getPermissions().get(0).getType()).isEqualTo(PermissionType.Viewer);
  }
}
