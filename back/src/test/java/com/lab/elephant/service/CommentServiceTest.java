package com.lab.elephant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lab.elephant.model.Comment;
import com.lab.elephant.model.Note;
import com.lab.elephant.model.User;
import com.lab.elephant.repository.CommentRepository;
import com.lab.elephant.repository.NoteRepository;
import com.lab.elephant.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class CommentServiceTest {

  @TestConfiguration
  static class CommentServiceImplTestContextConfiguration {
    @Autowired
    private CommentRepository commentRepository;

    @Bean
    public CommentService commentService() {
      return new CommentServiceImpl(commentRepository);
    }
  }

  @Autowired
  private CommentService commentService;
  @MockBean
  private CommentRepository commentRepository;
  @MockBean
  private NoteRepository noteRepository;
  @MockBean
  private UserRepository userRepository;

  @Test
  public void AddNewComment_WhenCommentCreated_ShouldReturnNewComment() throws JsonProcessingException {

    User user = new User("maxi", "perez", "maxi@gmail.com", "qwerty");
    Note note = new Note("Este es un titulo");
    Comment comment = new Comment("This is the content of the comment", user, note);

    Optional<User> optionalUser = Optional.of(user);
    Optional<Note> optionalNote = Optional.of(note);
    Optional<Comment> optionalComment = Optional.of(comment);

    Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(optionalUser);
    Mockito.when(userRepository.save(user)).thenReturn(user);

    Mockito.when(noteRepository.findById(note.getUuid())).thenReturn(optionalNote);
    Mockito.when(noteRepository.save(note)).thenReturn(note);

    Mockito.when(commentRepository.findById(comment.getUuid())).thenReturn(optionalComment);
    Mockito.when(commentRepository.save(comment)).thenReturn(comment);

    noteRepository.save(note);
    userRepository.save(user);

    final Comment commentTest = commentService.addComment(note, user, comment);

    assertThat(commentTest.getContent()).isEqualTo(comment.getContent());
    assertThat(commentTest.getUuid()).isEqualTo(comment.getUuid());
    assertThat(commentTest.getNote()).isEqualTo(note);
    assertThat(commentTest.getOwner()).isEqualTo(user);
    assertThat(note.getComments().contains(commentTest)).isTrue();
    assertThat(user.getComments().contains(commentTest)).isTrue();
  }

}