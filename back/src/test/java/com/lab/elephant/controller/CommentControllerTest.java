package com.lab.elephant.controller;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.elephant.model.Comment;
import com.lab.elephant.model.Note;
import com.lab.elephant.model.User;
import com.lab.elephant.security.UserDetailsServiceImpl;
import com.lab.elephant.service.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private UserServiceImpl userService;
  @MockBean
  private TokenServiceImpl tokenService;
  @MockBean
  private CommentServiceImpl commentService;
  @MockBean
  private NoteServiceImpl noteService;

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
  public void addNewComment_WhenCommentCreated_ShouldReturnNewComment() throws Exception {
    User user = new User("maxi", "perez", "maxi@gmail.com", "qwerty");
    Note note = new Note("Este es un titulo");
    Comment comment = new Comment("This is the content of the comment", user, note);

    user.addComment(comment);
    note.addComment(comment);

    Optional<User> optionalUser = Optional.of(user);
    Optional<Note> optionalNote = Optional.of(note);
    Optional<Comment> optionalComment = Optional.of(comment);

    given(userService.getUser(user.getUuid())).willReturn(optionalUser);
    given(userService.getByEmail(user.getEmail())).willReturn(optionalUser);
    given(userService.addUser(user)).willReturn(user);

    given(noteService.getNote(note.getUuid())).willReturn(optionalNote);
    given(noteService.addNote(note)).willReturn(note);

    given(commentService.getComment(comment.getUuid())).willReturn(optionalComment);
    given(commentService.addComment(note, user, comment)).willReturn(comment);

    String token = JWT.create().withSubject(user.getEmail())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));

    given(tokenService.getEmailByToken(token)).willReturn(user.getEmail());
    final String commentJson = objectMapper.writeValueAsString(comment);

    mvc.perform(post("/comment/add/" + note.getUuid()).content(commentJson)
            .header("Authorization", TOKEN_PREFIX + token)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
  }

  @Test
  public void addNewComment_WhenNoteNotExists_ShouldReturnNotFound() throws Exception {
    User user = new User("maxi", "perez", "maxi@gmail.com", "qwerty");
    Note note = new Note("Este es un titulo");
    Comment comment = new Comment("This is the content of the comment", user, note);

    user.addComment(comment);
    note.addComment(comment);

    Optional<User> optionalUser = Optional.of(user);
    Optional<Comment> optionalComment = Optional.of(comment);

    given(userService.getUser(user.getUuid())).willReturn(optionalUser);
    given(userService.getByEmail(user.getEmail())).willReturn(optionalUser);
    given(userService.addUser(user)).willReturn(user);
    given(commentService.getComment(comment.getUuid())).willReturn(optionalComment);
    given(commentService.addComment(note, user, comment)).willReturn(comment);

    String token = JWT.create().withSubject(user.getEmail())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));

    given(tokenService.getEmailByToken(token)).willReturn(user.getEmail());
    final String commentJson = objectMapper.writeValueAsString(comment);

    mvc.perform(post("/comment/add/" + note.getUuid()).content(commentJson)
            .header("Authorization", TOKEN_PREFIX + token)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound());
  }

  @Test
  public void addNewComment_WhenContentIsLargerThan300_ShouldReturnBadRequest() throws Exception {
    String content = "Este contenido contiene más de 300 letras, por lo cual debe tirar un error. " +
            "Este contenido contiene más de 300 letras, por lo cual debe tirar un error. " +
            "Este contenido contiene más de 300 letras, por lo cual debe tirar un error. " +
            "Este contenido contiene más de 300 letras, por lo cual debe tirar un error. " +
            "Este contenido contiene más de 300 letras, por lo cual debe tirar un error.";

    User user = new User("maxi", "perez", "maxi@gmail.com", "qwerty");
    Note note = new Note("Este es un titulo");
    Comment comment = new Comment(content, user, note);

    user.addComment(comment);
    note.addComment(comment);

    Optional<User> optionalUser = Optional.of(user);
    Optional<Note> optionalNote = Optional.of(note);
    Optional<Comment> optionalComment = Optional.of(comment);

    given(userService.getUser(user.getUuid())).willReturn(optionalUser);
    given(userService.getByEmail(user.getEmail())).willReturn(optionalUser);
    given(userService.addUser(user)).willReturn(user);

    given(noteService.getNote(note.getUuid())).willReturn(optionalNote);
    given(noteService.addNote(note)).willReturn(note);

    given(commentService.getComment(comment.getUuid())).willReturn(optionalComment);
    given(commentService.addComment(note, user, comment)).willReturn(comment);

    String token = JWT.create().withSubject(user.getEmail())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));

    given(tokenService.getEmailByToken(token)).willReturn(user.getEmail());
    final String commentJson = objectMapper.writeValueAsString(comment);

    mvc.perform(post("/comment/add/" + note.getUuid()).content(commentJson)
            .header("Authorization", TOKEN_PREFIX + token)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());
  }

}
