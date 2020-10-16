package com.lab.elephant.service;

import com.lab.elephant.model.*;
import com.lab.elephant.repository.PermissionRepository;
import com.lab.elephant.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class UserServiceTest {

  @TestConfiguration
  static class UserServiceImplTestContextConfiguration {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private NoteService noteService;
    @Bean
    public UserService userService() {
      return new UserServiceImpl(userRepository, passwordEncoder, permissionService, noteService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }
  }

  @Autowired
  private UserService userService;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private PermissionRepository permissionRepository;
  //these mockbeans are not used but needed for the test to run
  @MockBean
  private PermissionService permissionService;
  @MockBean
  private NoteService noteService;
  @Test
  public void AddUser_ShouldEncryptPassword() {
    String email = "john@elephant.com";
    String password = "foGMeyUAX34D13s2";
    User user = new User();
    user.setFirstName("John");
    user.setLastName("Smith");
    user.setPassword(password);
    user.setEmail(email);

    Mockito.when(userRepository.save(user)).thenReturn(user);
    Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    userService.addUser(user);
    final Optional<User> optionalUser = userService.getByEmail(email);
    assertThat(optionalUser.isPresent()).isTrue();
    assertThat(optionalUser.get().getPassword()).isNotEqualTo(password);
    assertThat(passwordEncoder.matches(password, optionalUser.get().getPassword())).isTrue();
  }


  @Test
  public void getByEmail_WhenEmailDoesExist() {
    User user1 = new User();
    User user2 = new User();

    String email = "john@elephant.com";

    user1.setFirstName("John");
    user1.setLastName("Smith");
    user1.setPassword("foGMeyUAX34D13s2");
    user1.setEmail(email);

    user2.setFirstName("Steve");
    user2.setLastName("Smith");
    user2.setPassword("dja891D11@");
    user2.setEmail("steve@elephant.com");
    Mockito.when(userRepository.save(user1)).thenReturn(user1);
    Mockito.when(userRepository.save(user2)).thenReturn(user2);

    Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user1));

    user1 = userService.addUser(user1);
    user2 = userService.addUser(user2);
    user1.setUuid(0);
    user2.setUuid(1);
    final Optional<User> user = userService.getByEmail(email);
    assertThat(user.isPresent()).isTrue();
    assertThat(user.get().getUuid()).isEqualTo(user1.getUuid());
    assertThat(user.get().getUuid()).isNotEqualTo(user2.getUuid());
  }

  @Test
  public void getByEmail_WhenEmailDoesNotExist() {
    User user1 = new User();
    User user2 = new User();

    user1.setFirstName("John");
    user1.setLastName("Smith");
    user1.setPassword("foGMeyUAX34D13s2");
    user1.setEmail("john@elephant.com");

    user2.setFirstName("Steve");
    user2.setLastName("Smith");
    user2.setPassword("dja891D11@");
    user2.setEmail("steve@elephant.com");

    user1 = userService.addUser(user1);
    user2 = userService.addUser(user2);
    Mockito.when(userRepository.findByEmail("hi@gmail.com")).thenReturn(Optional.empty());

    final Optional<User> user = userService.getByEmail("hi@gmail.com");
    assertThat(user.isPresent()).isFalse();
  }

  @Test
  public void updatePassword_WhenUserExists_ShouldUpdatePasswordAndBeEncrypted() {
    final User user = new User();
    final String email = "john@elephant.com";
    final String newPassword = "new password";
    final String oldPassword = "old password";
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(oldPassword));
    Mockito.when(userRepository.save(user)).thenReturn(user);
    Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    final Optional<User> optionalUser = userService.updatePassword(email, newPassword);

    assertThat(optionalUser.isPresent()).isTrue();
    assertThat(passwordEncoder.matches(newPassword, optionalUser.get().getPassword())).isTrue();
    assertThat(passwordEncoder.matches(oldPassword, optionalUser.get().getPassword())).isFalse();
  }

  @Test
  public void updatePassword_WhenUserDoesNotExist_ShouldReturnEmptyOptional() {
    final String nonexistentEmail = "john@elephant.com";
    final String newPassword = "new password";
    final Optional<User> optionalUser = userService.updatePassword(nonexistentEmail, newPassword);

    assertThat(optionalUser.isPresent()).isFalse();
  }

  @Test
  public void editUser_WhenUserExists_ShouldEditFirstNameAndLastName() {
    final User user = new User();
    user.setUuid(1);
    final String email = "john@elephant.com";
    final String oldFirstName = "jHON";
    final String oldLastName = "eLEPHANT";
    final String newFirstName = "John";
    final String newLastName = "Elephant";
    final EditUserDTO dto = new EditUserDTO(newFirstName, newLastName);
    user.setFirstName(oldFirstName);
    user.setLastName(oldLastName);

    Mockito.when(userRepository.save(user)).thenReturn(user);
    Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    final Optional<User> optionalUser = userService.editUser(email, dto);

    assertThat(optionalUser.isPresent()).isTrue();
    final User returnedUser = optionalUser.get();
    assertThat(optionalUser.get().getUuid()).isEqualTo(user.getUuid());
    assertThat(returnedUser.getFirstName()).isEqualTo(newFirstName);
    assertThat(returnedUser.getLastName()).isEqualTo(newLastName);
  }

  @Test
  public void editUser_WhenUserDoesNotExist_ShouldReturnEmptyOptional() {
    final String nonexistentEmail = "john@elephant.com";
    final EditUserDTO dto = new EditUserDTO("name", "surname");
    final Optional<User> optionalUser = userService.editUser(nonexistentEmail, dto);

    assertThat(optionalUser.isPresent()).isFalse();
  }

  @Test
  public void getAllNotesByUser_whenUserExists_ShouldReturnAllNotes() {
    User user = new User("a", "b", "a@b", "p");

    Note note1 = new Note("title1");
    Note note2 = new Note("title2");
    Note note3 = new Note("title3");

    List<Permission> permissions = new ArrayList<>();
    permissions.add(new Permission(user, note1, PermissionType.Owner));
    permissions.add(new Permission(user, note2, PermissionType.Editor));
    permissions.add(new Permission(user, note3, PermissionType.Viewer));

    Mockito.when(permissionService.findAllByUser(user)).thenReturn(permissions);
    Mockito.when(userRepository.save(user)).thenReturn(user);

    List<Note> notes = userService.getAllNotesByUser(user);

    assertThat(notes.size()).isEqualTo(3);
    assertThat(notes.get(0).getUuid()).isEqualTo(note1.getUuid());
    assertThat(notes.get(1).getUuid()).isEqualTo(note2.getUuid());
    assertThat(notes.get(2).getUuid()).isEqualTo(note3.getUuid());
  }
  
  @Test
  public void delete_ShouldDeleteUserAndAllTheirNotes() {
    final User user = new User();
    final long id = 1;
    final Note note1 = new Note();
    final Note note2 = new Note();
    final Note note3 = new Note();
  
    note1.setUuid(2);
    note2.setUuid(3);
    note3.setUuid(4);
    
    final Permission p1 = new Permission(user, note1, PermissionType.Owner);
    final Permission p2 = new Permission(user, note2, PermissionType.Editor);
    final Permission p3 = new Permission(user, note3, PermissionType.Viewer);
    final List<Permission> permissions = new ArrayList<>();
    
    permissions.add(p1);
    permissions.add(p2);
    permissions.add(p3);
    
    Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
    Mockito.when(permissionService.findAllByUser(user)).thenReturn(permissions);
    
    userService.delete(id);
    
    Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    Mockito.verify(noteService, Mockito.times(1)).deleteNote(note1.getUuid());
    Mockito.verify(noteService, Mockito.times(1)).deleteNote(note2.getUuid());
    Mockito.verify(noteService, Mockito.times(1)).deleteNote(note3.getUuid());
  }
}
