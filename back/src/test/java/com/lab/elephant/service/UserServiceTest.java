package com.lab.elephant.service;

import com.lab.elephant.model.User;
import com.lab.elephant.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    assertThat(optionalUser.get().getPassword()).isEqualTo(passwordEncoder.encode(password));
  }
  
  
  @Test
  public void getByEmail_WhenEmailDoesExist() {
    final User user1 = new User();
    final User user2 = new User();
    
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
  
    userService.addUser(user1);
    userService.addUser(user2);
    
    final Optional<User> user = userService.getByEmail(email);
    assertThat(user.isPresent()).isTrue();
    assertThat(user.get().getUuid()).isEqualTo(user1.getUuid());
    assertThat(user.get().getUuid()).isNotEqualTo(user2.getUuid());
  }
  
  @Test
  public void getByEmail_WhenEmailDoesNotExist() {
    final User user1 = new User();
    final User user2 = new User();
    
    user1.setFirstName("John");
    user1.setLastName("Smith");
    user1.setPassword("foGMeyUAX34D13s2");
    user1.setEmail("john@elephant.com");
    
    user2.setFirstName("Steve");
    user2.setLastName("Smith");
    user2.setPassword("dja891D11@");
    user2.setEmail("steve@elephant.com");
    
    userService.addUser(user1);
    userService.addUser(user2);
    Mockito.when(userRepository.findByEmail("hi@gmail.com")).thenReturn(Optional.of(null));
  
    final Optional<User> user = userService.getByEmail("hi@gmail.com");
    assertThat(user.isPresent()).isFalse();
  }
}
