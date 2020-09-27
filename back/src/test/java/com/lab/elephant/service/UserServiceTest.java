package com.lab.elephant.service;

import com.lab.elephant.model.EditUserDTO;
import com.lab.elephant.model.User;
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
    @Bean
    public UserService userService() {
      return new UserServiceImpl(userRepository, passwordEncoder);
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
    final EditUserDTO dto= new EditUserDTO(newFirstName, newLastName);
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
}
