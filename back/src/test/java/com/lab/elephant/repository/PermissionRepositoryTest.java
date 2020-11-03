package com.lab.elephant.repository;

import com.lab.elephant.model.Note;
import com.lab.elephant.model.Permission;
import com.lab.elephant.model.PermissionType;
import com.lab.elephant.model.User;
import com.lab.elephant.service.NoteService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PermissionRepositoryTest {

  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private NoteRepository noteRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PermissionRepository permissionRepository;
  @Autowired
  private NoteService noteService;

  @Before
  public void deleteDB() {
    commentRepository.deleteAll();
  }

  @Test
  public void findAllPermissionsByUser_WhenAllCreatedNotInOrder_ShouldGetItInOrder() {
    User user = new User("a", "b", "a@b", "p");

    Note note1 = new Note("title1");
    Note note2 = new Note("title2");

    userRepository.save(user);
    noteRepository.save(note1);
    noteRepository.save(note2);

    noteService.addNote(note1, user);
    noteService.addNote(note2, user);

    List<Permission> permissions = permissionRepository.findAllByUser(user);

    assertThat(permissions.get(0).getNote().getUuid()).isEqualTo(note1.getUuid());
    assertThat(permissions.get(1).getNote().getUuid()).isEqualTo(note2.getUuid());
  }

  @Test
  public void findByUserAndNote_WhenPermissionExists_ShouldReturnOptionalOfThatPermission() {
    final User user = new User("firstName", "lastName", "em@ail", "password");
    final Note note = new Note("title1");
    final Note otherNote = new Note("otherNote");
    userRepository.save(user);
    noteRepository.save(note);
    noteRepository.save(otherNote);
    final Permission permission = new Permission(user, note, PermissionType.Owner);
    
    permissionRepository.save(permission);
    permissionRepository.save(new Permission(user, otherNote, PermissionType.Viewer));
    
    final Optional<Permission> optionalPermission = permissionRepository.findByUserAndNote(user, note);
    assertThat(optionalPermission.isPresent()).isTrue();
    assertThat(optionalPermission.get().getUuid()).isEqualTo(permission.getUuid());
  }
  
  @Test
  public void findByUserAndNote_WhenPermissionDoesntExist_ShouldReturnEmptyOptional() {
    final User user = new User("firstName", "lastName", "em@ail", "password");
    final Note note = new Note("title1");
    userRepository.save(user);
    noteRepository.save(note);
    final Optional<Permission> p = permissionRepository.findByUserAndNote(user, note);
    assertThat(p.isPresent()).isFalse();
  }
}
