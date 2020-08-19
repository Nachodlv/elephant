package com.lab.elephant.service;

import com.lab.elephant.repository.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UserServiceTest {
  
  @TestConfiguration
  static class UserServiceImplTestContextConfiguration {
    @Autowired
    private UserRepository userRepository;
    @Bean
    public UserService userService() {
      return new UserServiceImpl(userRepository);
    }
  }
  
  @Autowired
  private UserService userService;
  @MockBean
  private UserRepository userRepository;
  
  // No tests implemented due to userService not having any unique methods.
}
