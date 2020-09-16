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

import java.sql.Timestamp;
import java.util.*;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.lab.elephant.security.SecurityConstants.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
  @MockBean
  private BlackListedTokenServiceImpl blackListedTokenService;

  @Test
  public void addNewComment_WhenCommentCreated_ShouldReturnNewComment() throws Exception {
    User user = new User("maxi", "perez", "maxi@gmail.com", "qwerty");
    Note note = new Note("Este es un titulo");
    Timestamp timestamp = new Timestamp(new Date().getTime());
    Comment comment = new Comment("This is the content of the comment", user, note, timestamp);

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
    Timestamp timestamp = new Timestamp(new Date().getTime());
    Comment comment = new Comment("This is the content of the comment", user, note, timestamp);

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
    Timestamp timestamp = new Timestamp(new Date().getTime());
    Comment comment = new Comment(content, user, note, timestamp);

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

  @Test
  public void getAllCommentsInOrderByNote_WhenCommentsAreCreated_ShouldReturnCommentsInOrder() throws Exception {
    Timestamp time1 = new Timestamp(new Date(2020, Calendar.FEBRUARY, 1).getTime());
    Timestamp time2 = new Timestamp(new Date(2020, Calendar.DECEMBER, 10).getTime());
    Timestamp time3 = new Timestamp(new Date(2021, Calendar.JULY, 25).getTime());

    Note note = new Note("title1");
    User owner = new User("a", "b", "a@b", "p");

    Comment comment1 = new Comment("content1", owner, note, time1);
    Comment comment2 = new Comment("content2", owner, note, time3);
    Comment comment3 = new Comment("content2", owner, note, time2);

    userService.addUser(owner);

    noteService.addNote(note);

    commentService.addComment(note, owner, comment3);
    commentService.addComment(note, owner, comment1);
    commentService.addComment(note, owner, comment2);

    List<Comment> comments = Arrays.asList(comment2, comment3, comment1);

    given(commentService.getAllCommentsByNote(note)).willReturn(comments);
    given(noteService.getNote(note.getUuid())).willReturn(Optional.of(note));

    mvc.perform(get("/comment/all/" + note.getUuid())
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
  }

  @Test
  public void getAllCommentsInOrderByNote_WhenNoteIsNotCreated_ShouldReturnNoteNotFound() throws Exception {
    Timestamp time1 = new Timestamp(new Date(2020, Calendar.FEBRUARY, 1).getTime());
    Timestamp time2 = new Timestamp(new Date(2020, Calendar.DECEMBER, 10).getTime());
    Timestamp time3 = new Timestamp(new Date(2021, Calendar.JULY, 25).getTime());

    Note note = new Note("title1");
    User owner = new User("a", "b", "a@b", "p");

    Comment comment1 = new Comment("content1", owner, note, time1);
    Comment comment2 = new Comment("content2", owner, note, time3);
    Comment comment3 = new Comment("content2", owner, note, time2);

    userService.addUser(owner);

    commentService.addComment(note, owner, comment3);
    commentService.addComment(note, owner, comment1);
    commentService.addComment(note, owner, comment2);

    List<Comment> comments = Arrays.asList(comment2, comment3, comment1);

    given(commentService.getAllCommentsByNote(note)).willReturn(comments);

    mvc.perform(get("/comment/all/" + note.getUuid())
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound());
  }

}
