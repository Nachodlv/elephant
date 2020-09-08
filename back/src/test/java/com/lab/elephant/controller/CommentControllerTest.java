package com.lab.elephant.controller;

import com.lab.elephant.model.Comment;
import com.lab.elephant.security.UserDetailsServiceImpl;
import com.lab.elephant.service.CommentServiceImpl;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTest {

  @Autowired
  private MockMvc mvc;
  @MockBean
  private UserServiceImpl userService;
  @MockBean
  private TokenServiceImpl tokenService;
  @MockBean
  private CommentServiceImpl commentService;

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
  public void addNewComment_WhenCommentCreated_ShouldReturnNewComment() {
    Comment comment = new Comment("This is the content of the comment");
    Optional<Comment> optionalComment = Optional.of(comment);

//    given(commentService.getNote(note.getUuid())).willReturn(optionalNote);
//
//    final String noteJson = objectMapper.writeValueAsString(note);
//    mvc.perform(post("/note/new").content(noteJson)
//            .contentType(MediaType.APPLICATION_JSON))
//            .andDo(MockMvcResultHandlers.print())
//            .andExpect(status().isOk());

  }

  @Test
  public void addNewComment_WhenNoteNotExists_ShouldReturnNotFound() {

  }

  @Test
  public void addNewComment_WhenContentIsLargerThan300_ShouldReturnBadRequest() {
    String content = "Este contenido contiene más de 300 letras, por lo cual debe tirar un error. " +
            "Este contenido contiene más de 300 letras, por lo cual debe tirar un error. " +
            "Este contenido contiene más de 300 letras, por lo cual debe tirar un error. " +
            "Este contenido contiene más de 300 letras, por lo cual debe tirar un error. " +
            "Este contenido contiene más de 300 letras, por lo cual debe tirar un error.";


  }

}
