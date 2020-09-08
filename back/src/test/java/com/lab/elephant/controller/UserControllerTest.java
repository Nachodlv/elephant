package com.lab.elephant.controller;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.elephant.model.User;
import com.lab.elephant.security.UserDetailsServiceImpl;
import com.lab.elephant.service.TokenService;
import com.lab.elephant.service.TokenServiceImpl;
import com.lab.elephant.service.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Date;
import java.util.Optional;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.lab.elephant.security.SecurityConstants.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
  @Autowired
  private MockMvc mvc;
  @MockBean
  private UserServiceImpl userService;
  @MockBean
  private TokenServiceImpl tokenService;

  @TestConfiguration
  static class TokenServiceImplTestContextConfiguration {
    @Bean
    public TokenService tokenService() {
      return new TokenServiceImpl();
    }
  }

  // Both UserDetailsServiceImpl and BCryptPasswordEncoder
  // are not used but are necessary for the tests to run.
  @MockBean
  private UserDetailsServiceImpl userDetailsService;
  @MockBean
  private BCryptPasswordEncoder passwordEncoder;

  @Test
  public void addUser_whenEmailDoesNotExist_ShouldReturnOk() throws Exception {
    User user = new User();
    user.setFirstName("John");
    user.setLastName("Smith");
    user.setPassword("foGMeyUAX34D13s2");
    user.setEmail("john@elephant.com");

    ObjectMapper o = new ObjectMapper();
    final String json = o.writeValueAsString(user);
    mvc.perform(post("/user/create").content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
  }

  @Test
  public void addUser_whenEmailDoesExist_ShouldReturn409() throws Exception {

    String email = "john@elephant.com";
    User user = new User();
    user.setFirstName("John");
    user.setLastName("Smith");
    user.setPassword("foGMeyUAX34D13s2");
    user.setEmail(email);
    Optional<User> oUser = Optional.of(user);
    given(userService.getByEmail(email)).willReturn(oUser);
    ObjectMapper o = new ObjectMapper();
    final String json = o.writeValueAsString(user);
    mvc.perform(post("/user/create").content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isConflict());
  }

  @Test
  public void addUser_whenUserIsNull_ShouldReturnBadRequest() throws Exception {
    User user = null;
    ObjectMapper o = new ObjectMapper();
    final String json = o.writeValueAsString(user);
    mvc.perform(post("/user/create").content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());
  }

  @Test
  public void getUser_whenUserDoesExist_ShouldReturnTheUser() throws Exception {
    User user = new User("maxi", "perez", "maxi@gmail.com", "qwerty");

    userService.addUser(user);

    ObjectMapper o = new ObjectMapper();
    final String noteJson = o.writeValueAsString(user);

    given(userService.getUser(1L)).willReturn(Optional.of(user));
    given(userService.getByEmail(user.getEmail())).willReturn(Optional.of(user));

    String token = JWT.create().withSubject(user.getEmail())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));

    given(tokenService.getEmailByToken(token)).willReturn(user.getEmail());

    mvc.perform(get("/user").content(noteJson)
            .header("Authorization", TOKEN_PREFIX + token)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
  }

  @Test
  public void getUser_whenTokenNotSend_ShouldReturnBadRequest() throws Exception {
    User user = new User("maxi", "perez", "maxi@gmail.com", "qwerty");

    userService.addUser(user);

    ObjectMapper o = new ObjectMapper();
    final String noteJson = o.writeValueAsString(user);
    given(userService.getUser(1L)).willReturn(Optional.of(user));

    mvc.perform(get("/user").content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());
  }

  @Test
  public void getUser_whenUserNotExists_ShouldReturnNotFound() throws Exception {
    User user = new User("maxi", "perez", "maxi@gmail.com", "qwerty");

    String token = JWT.create().withSubject(user.getEmail())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));

    ObjectMapper o = new ObjectMapper();
    final String noteJson = o.writeValueAsString(user);
    given(userService.getUser(1L)).willReturn(Optional.of(user));

    mvc.perform(get("/user").content(noteJson)
            .header("Authorization", TOKEN_PREFIX + token)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound());
  }

}
