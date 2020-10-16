package com.lab.elephant.controller;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.elephant.model.EditUserDTO;
import com.lab.elephant.model.Note;
import com.lab.elephant.model.UpdatePasswordDto;
import com.lab.elephant.model.User;
import com.lab.elephant.security.UserDetailsServiceImpl;
import com.lab.elephant.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.*;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.lab.elephant.security.SecurityConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
  @MockBean
  private NoteServiceImpl noteService;

  private final ObjectMapper o = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);

  @TestConfiguration
  static class TokenServiceImplTestContextConfiguration {
    @Bean
    public TokenService tokenService() {
      return new TokenServiceImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }
  }
  @Autowired
  private BCryptPasswordEncoder passwordEncoder;
  
  // All the MockBeans bellow are not used but necessary for the test to run
  // are not used but are necessary for the tests to run.
  @MockBean
  private UserDetailsServiceImpl userDetailsService;
  @MockBean
  private BlackListedTokenServiceImpl blackListedTokenService;
  @MockBean
  private EmailService emailService;
  
  @Test
  public void addUser_whenEmailDoesNotExist_ShouldReturnOk() throws Exception {
    User user = new User();
    user.setFirstName("John");
    user.setLastName("Smith");
    user.setPassword("foGMeyUAX34D13s2");
    user.setEmail("john@elephant.com");

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
    final String json = o.writeValueAsString(user);
    mvc.perform(post("/user/create").content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isConflict());
  }

  @Test
  public void addUser_whenUserIsNull_ShouldReturnBadRequest() throws Exception {
    User user = null;
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

    final String noteJson = o.writeValueAsString(user);
    given(userService.getUser(1L)).willReturn(Optional.of(user));

    mvc.perform(get("/user").content(noteJson)
            .header("Authorization", TOKEN_PREFIX + token)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound());
  }

  @Test
  public void updatePassword_WithCorrectPassword_ShouldReturn200() throws Exception {
    final User user = new User();
    final String oldPassword = "oldPassword";
    final String newPassword = "newPassword";
    final UpdatePasswordDto dto = new UpdatePasswordDto(oldPassword, newPassword);
    final String json = new ObjectMapper().writeValueAsString(dto);

    user.setPassword(passwordEncoder.encode(oldPassword));

    mockUserAuthentication();
    given(userService.getByEmail("user")).willReturn(Optional.of(user));

    mvc.perform(put("/user/updatePassword").content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }

  @Test
  public void updatePassword_WithIncorrectPassword_ShouldReturn401() throws Exception {
    final User user = new User();
    final String userPassword = "userPassword";
    final String incorrectPassword = "incorrectPassword";
    final String newPassword = "newPassword";
    final UpdatePasswordDto dto = new UpdatePasswordDto(incorrectPassword, newPassword);
    final String json = new ObjectMapper().writeValueAsString(dto);

    user.setPassword(passwordEncoder.encode(userPassword));

    mockUserAuthentication();
    given(userService.getByEmail("user")).willReturn(Optional.of(user));

    mvc.perform(put("/user/updatePassword").content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(status().reason("Incorrect Password"));
  }

  @Test
  public void editUser_WithEverythingOk_ShouldReturn200() throws Exception {
    final User user = new User();
    final String newFirstName = "John";
    final String newLastName = "Elephant";
    final EditUserDTO dto = new EditUserDTO(newFirstName, newLastName);
    final ObjectMapper o = new ObjectMapper();
    final String json = o.writeValueAsString(dto);

    //this is mocking the user Authentication
    mockUserAuthentication();
    given(userService.getByEmail("user")).willReturn(Optional.of(user));

    mvc.perform(put("/user/editUser").content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }

  @Test
  public void editUser_WithInvalidDTO_ShouldReturn400() throws Exception {
    final ObjectMapper o = new ObjectMapper();
    final String json = o.writeValueAsString(new EditUserDTO());

    //this is mocking the user Authentication
    mockUserAuthentication();
    given(userService.getByEmail("user")).willReturn(Optional.of(new User()));

    mvc.perform(put("/user/editUser").content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void getAllNotesByUser_whenUserExists_ShouldReturnAllNotes() throws Exception {
    User user = new User("maxi", "perez", "maxi@gmail.com", "qwerty");
    Note note1 = new Note("title1");
    Note note2 = new Note("title1");
    Note note3 = new Note("title1");

    userService.addUser(user);
    noteService.addNote(note1, user);
    noteService.addNote(note2, user);
    noteService.addNote(note3, user);
    mockUserAuthentication();

    given(userService.getByEmail("user")).willReturn(Optional.of(user));

    given(userService.getAllNotesByUser(user)).willReturn(Arrays.asList(note1, note2, note3));
    given(userService.getUser(1L)).willReturn(Optional.of(user));
    given(userService.addUser(user)).willReturn(user);

    given(noteService.getNote(note1.getUuid())).willReturn(Optional.of(note1));
    given(noteService.getNote(note2.getUuid())).willReturn(Optional.of(note2));
    given(noteService.getNote(note3.getUuid())).willReturn(Optional.of(note3));
    given(noteService.getUsersWithPermissions(note1)).willReturn(Collections.singletonList(user));
    given(noteService.getUsersWithPermissions(note2)).willReturn(Collections.singletonList(user));
    given(noteService.getUsersWithPermissions(note3)).willReturn(Collections.singletonList(user));

    String token = JWT.create().withSubject(user.getEmail())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));

    given(tokenService.getEmailByToken(token)).willReturn(user.getEmail());

    MvcResult result = mvc.perform(get("/user/notes")
            .header("Authorization", TOKEN_PREFIX + token)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk()).andReturn();

    String json = result.getResponse().getContentAsString();

    List<Note> notes = o.readValue(json, new TypeReference<List<Note>>() {
    });

    assertThat(notes.size()).isEqualTo(3);
    assertThat(notes.get(0).getUuid()).isEqualTo(note1.getUuid());
    assertThat(notes.get(1).getUuid()).isEqualTo(note2.getUuid());
    assertThat(notes.get(2).getUuid()).isEqualTo(note3.getUuid());
  }

  @Test
  public void getAllNotesByUser_whenUserNotExists_ShouldReturnNotFound() throws Exception {
    User user = new User("maxi", "perez", "maxi@gmail.com", "qwerty");

    //this is mocking the user Authentication
    mockUserAuthentication();
    given(userService.getByEmail("user")).willReturn(Optional.empty());

    String token = JWT.create().withSubject(user.getEmail())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));

    final String noteJson = o.writeValueAsString(user);
    given(userService.getUser(1L)).willReturn(Optional.of(user));

    mvc.perform(get("/user/notes").content(noteJson)
            .header("Authorization", TOKEN_PREFIX + token)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound());
  }

  private void mockUserAuthentication() {
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
  }
  
  @Test
  public void deleteUser_WhenEverythingIsOk_ShouldReturn200() throws Exception {
    final String password = "strong password";
    final User user = new User();
    user.setPassword(passwordEncoder.encode(password));
    
    //this is mocking the user Authentication
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(user));
    
    mvc.perform(delete("/user?password=" + password))
            .andExpect(status().isOk());
  }
  
  @Test
  public void deleteUser_WhenPasswordIsWrong_ShouldReturn401() throws Exception {
    final String password = "strong password";
    final User user = new User();
    user.setPassword(passwordEncoder.encode("wrong password"));
    
    //this is mocking the user Authentication
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(user));
    
    mvc.perform(delete("/user?password=" + password))
            .andExpect(status().isUnauthorized())
            .andExpect(status().reason("Incorrect Password"));
  }
}
