package com.lab.elephant.security;

import com.lab.elephant.model.User;
import com.lab.elephant.model.UserDetailsImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(JWTAuthenticationFilter.class)
 public class LogInTest {
  @Autowired
  private MockMvc mvc;
  @MockBean
  private UserDetailsServiceImpl userDetailsService;
  @Autowired
  private BCryptPasswordEncoder passwordEncoder;
  
  
  @Test
  public void logIn_withValidUser_ShouldReturn_200() throws Exception {
    final String email = "john@elephant.com";
    final String password = "5tr0ng p455w0rd";
    final String json = "{\n" +
            "    \"email\": \"" + email + "\",\n" +
            "    \"password\": \"" +  password + "\"\n" + "}";
    final User user = new User();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    final UserDetailsImpl ud = new UserDetailsImpl(user);
    given(userDetailsService.loadUserByUsername(email)).willReturn(ud);
    mvc.perform(post("/login").content(json).header("Content-Type", "application/json"))
            .andExpect(status().isOk());
  }
  
  @Test
  public void logIn_withInvalidUser_ShouldReturn_401() throws Exception {
    final String email = "john@elephant.com";
    final String password = "5tr0ng p455w0rd";
    final String json = "{\n" +
            "    \"email\": \"" + email + "\",\n" +
            "    \"password\": \"" +  password + "\"\n" + "}";
    mvc.perform(post("/login").content(json).header("Content-Type", "application/json"))
            .andExpect(status().isUnauthorized());
  }
  
  @Test
  public void logIn_withIncorrectPassword_ShouldReturn_401() throws Exception {
    final String email = "john@elephant.com";
    final String password = "5tr0ng p455w0rd";
    final String wrongPassword = "123456";
    final String json = "{\n" +
            "    \"email\": \"" + email + "\",\n" +
            "    \"password\": \"" +  wrongPassword + "\"\n" + "}";
    final User user = new User();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    final UserDetailsImpl ud = new UserDetailsImpl(user);
    given(userDetailsService.loadUserByUsername(email)).willReturn(ud);
    mvc.perform(post("/login").content(json).header("Content-Type", "application/json"))
            .andExpect(status().isUnauthorized());
  }
}