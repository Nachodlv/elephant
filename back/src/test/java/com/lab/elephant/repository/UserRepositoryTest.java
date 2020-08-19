package com.lab.elephant.repository;

import com.lab.elephant.model.User;
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
  
  @Test
  public void findByEmailTest() {
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
    
    userRepository.save(user1);
    userRepository.save(user2);
  
    final Optional<User> user = userRepository.findByEmail(email);
    assertThat(user.isPresent()).isTrue();
    assertThat(user.get()).isSameAs(userRepository.findById(1L).get());
    assertThat(user.get()).isNotSameAs(userRepository.findById(2L).get());
//    assertThat(user).isEqualTo(userRepository.findById(1L));
//    assertThat(user).isNotEqualTo(userRepository.findById(2L));
  }
}
