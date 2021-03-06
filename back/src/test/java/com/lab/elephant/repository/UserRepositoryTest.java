package com.lab.elephant.repository;

import com.lab.elephant.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserRepositoryTest {
  
  @Autowired
  private UserRepository userRepository;
  @Before
  public void deleteDB() {
    userRepository.deleteAll();
  }
  @Test
  public void findByEmail_WhenEmailDoesExist() {
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
    
    user1 = userRepository.save(user1);
    user2 = userRepository.save(user2);
    final Optional<User> user = userRepository.findByEmail(email);
    assertThat(user.isPresent()).isTrue();
    assertThat(user.get().getUuid()).isEqualTo(user1.getUuid());
    assertThat(user.get().getUuid()).isNotEqualTo(user2.getUuid());
  }
  
  @Test
  public void findByEmail_WhenEmailDoesNotExist() {
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
  
    userRepository.save(user1);
    userRepository.save(user2);
  
    final Optional<User> user = userRepository.findByEmail("hi@gmail.com");
    assertThat(user.isPresent()).isFalse();
  }
}
