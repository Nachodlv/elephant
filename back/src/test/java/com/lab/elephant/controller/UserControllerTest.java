package com.lab.elephant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.elephant.model.User;
import com.lab.elephant.security.UserDetailsServiceImpl;
import com.lab.elephant.service.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
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
  public void addUser_whenUserisNull_ShouldReturnBadRequest() throws Exception {
    User user = null;
    ObjectMapper o = new ObjectMapper();
    final String json = o.writeValueAsString(user);
    mvc.perform(post("/user/create").content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());
  }
}
