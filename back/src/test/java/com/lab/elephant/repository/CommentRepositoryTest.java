package com.lab.elephant.repository;

import com.lab.elephant.model.Comment;
import com.lab.elephant.model.Note;
import com.lab.elephant.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CommentRepositoryTest {

  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private NoteRepository noteRepository;
  @Autowired
  private UserRepository userRepository;

  @Before
  public void deleteDB() {
    commentRepository.deleteAll();
  }

  @Test
  public void findAllByNoteOrderByCreatedDesc_WhenAllCreatedNotInOrder_ShouldGetItInOrder() {
    Timestamp time1 = new Timestamp(java.sql.Date.valueOf(LocalDate.of(2020, Calendar.FEBRUARY, 1)).getTime());
    Timestamp time2 = new Timestamp(java.sql.Date.valueOf(LocalDate.of(2020, Calendar.DECEMBER, 10)).getTime());
    Timestamp time3 = new Timestamp(java.sql.Date.valueOf(LocalDate.of(2021, Calendar.JULY, 25)).getTime());

    Note note = new Note("title1");
    Note note2 = new Note("title2");
    User owner = new User("a", "b", "a@b", "p");

    Comment comment1 = new Comment("content1", owner, note, time1);
    Comment comment2 = new Comment("content2", owner, note, time3);
    Comment comment3 = new Comment("content2", owner, note, time2);

    userRepository.save(owner);

    noteRepository.save(note2);
    noteRepository.save(note);

    commentRepository.save(comment3);
    commentRepository.save(comment1);
    commentRepository.save(comment2);

    List<Comment> comments = commentRepository.findAllByNoteOrderByCreatedDesc(note);

//    Compare it as those are in Asc Order when were saved in different order and different Date created
    assertThat(comments.size()).isEqualTo(3);
    assertThat(comments.get(0).getUuid()).isEqualTo(comment2.getUuid());
    assertThat(comments.get(1).getUuid()).isEqualTo(comment3.getUuid());
    assertThat(comments.get(2).getUuid()).isEqualTo(comment1.getUuid());
  }

}
