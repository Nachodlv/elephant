package com.lab.elephant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lab.elephant.model.Comment;
import com.lab.elephant.model.CommentDTO;
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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

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
    Timestamp timestamp = new Timestamp(new Date().getTime());
    Comment comment = new Comment("This is the content of the comment", user, note, timestamp);

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

    final CommentDTO commentTest = commentService.addComment(note, user, comment);

    assertThat(commentTest.getContent()).isEqualTo(comment.getContent());
    assertThat(commentTest.getUuid()).isEqualTo(comment.getUuid());
    assertThat(commentTest.getOwnerName()).isEqualTo(user.getFirstName() + " " + user.getLastName());
    assertThat(note.getComments().contains(comment)).isTrue();
    assertThat(user.getComments().contains(comment)).isTrue();
  }

  @Test
  public void GetCommentById_WhenCommentDoesExist_ShouldReturnComment() {

    User user = new User("maxi", "perez", "maxi@gmail.com", "qwerty");
    Note note = new Note("Este es un titulo");
    Timestamp timestamp = new Timestamp(new Date().getTime());
    Comment comment = new Comment("This is the content of the comment", user, note, timestamp);

    Mockito.when(commentRepository.save(comment)).thenReturn(comment);
    Mockito.when(commentRepository.findById(comment.getUuid())).thenReturn(Optional.of(comment));

    commentRepository.save(comment);

    Optional<Comment> optionalComment = commentService.getComment(comment.getUuid());
    Comment commentTest = null;

    if (optionalComment.isPresent()) {
      commentTest = optionalComment.get();
    }

    assertThat(commentTest).isNotNull();
    assertThat(commentTest).isEqualTo(comment);
    assertThat(Objects.requireNonNull(commentTest).getUuid()).isEqualTo(comment.getUuid());
    assertThat(commentTest.getContent()).isEqualTo(comment.getContent());
  }

  @Test
  public void findAllByNoteOrderByCreatedDesc_WhenAllCreatedNotInOrder_ShouldGetItInOrder() {
    Timestamp time1 = new Timestamp(java.sql.Date.valueOf(LocalDate.of(2020, Calendar.FEBRUARY, 1)).getTime());
    Timestamp time2 = new Timestamp(java.sql.Date.valueOf(LocalDate.of(2020, Calendar.DECEMBER, 10)).getTime());
    Timestamp time3 = new Timestamp(java.sql.Date.valueOf(LocalDate.of(2021, Calendar.JULY, 25)).getTime());

    Note note = new Note("title1");
    User owner = new User("a", "b", "a@b", "p");

    Comment comment1 = new Comment("content1", owner, note, time1);
    Comment comment2 = new Comment("content2", owner, note, time3);
    Comment comment3 = new Comment("content2", owner, note, time2);

    userRepository.save(owner);

    noteRepository.save(note);

    commentRepository.save(comment3);
    commentRepository.save(comment1);
    commentRepository.save(comment2);

    Mockito.when(userRepository.save(owner)).thenReturn(owner);
    Mockito.when(noteRepository.save(note)).thenReturn(note);
    Mockito.when(commentRepository.save(comment1)).thenReturn(comment1);
    Mockito.when(commentRepository.save(comment2)).thenReturn(comment2);
    Mockito.when(commentRepository.save(comment3)).thenReturn(comment3);

    List<Comment> commentsAsList = Arrays.asList(comment2, comment3, comment1);

    Mockito.when(commentRepository.findAllByNoteOrderByCreatedDesc(note)).thenReturn(commentsAsList);

    List<CommentDTO> comments = commentService.getAllCommentsByNote(note);

//    Compare it as those are in Asc Order when were saved in different order and different Date created
    assertThat(comments.size()).isEqualTo(3);
    assertThat(comments.get(0).getUuid()).isEqualTo(comment2.getUuid());
    assertThat(comments.get(1).getUuid()).isEqualTo(comment3.getUuid());
    assertThat(comments.get(2).getUuid()).isEqualTo(comment1.getUuid());
  }

}
