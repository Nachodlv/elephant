package com.lab.elephant.service;

import com.lab.elephant.model.Note;
import com.lab.elephant.model.Permission;
import com.lab.elephant.model.PermissionType;
import com.lab.elephant.model.User;
import com.lab.elephant.repository.NoteRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class NoteServiceTest {

  @TestConfiguration
  static class NoteServiceImplTestContextConfiguration {
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private PermissionService permissionService;
    @Bean
    public NoteService employeeService() {
      return new NoteServiceImpl(noteRepository, permissionService);
    }
  }

  @Autowired
  private NoteService noteService;
  @MockBean
  private NoteRepository noteRepository;
  //this is not used but needed for the Application Context to load
  @MockBean
  private PermissionService permissionService;
  
  @Test
  public void AddNote_WhenAddingANewNote_ShouldReturnANewNote() {

    Note note = new Note("Nueva Nota");

    Mockito.when(noteRepository.save(note)).thenReturn(note);
    Mockito.when(noteRepository.findById(note.getUuid())).thenReturn(Optional.of(note));
    noteService.addNote(note, new User());

    Optional<Note> optionalNote = noteRepository.findById(note.getUuid());

    assertThat(optionalNote.isPresent()).isTrue();
    assertThat(optionalNote.get().getTitle()).isEqualTo(note.getTitle());
    assertThat(optionalNote.get().getContent()).isEqualTo(note.getContent());
    assertThat(optionalNote.get().getCreated()).isNotNull();
  }

  @Test
  public void AddNote_WhenGettingANewNote_ShouldGetTheNewNote() {

    Note note1 = new Note("Nueva Nota");
    Note note2 = new Note("Nueva Nota");

    Mockito.when(noteRepository.save(note1)).thenReturn(note1);
    Mockito.when(noteRepository.findById(note1.getUuid())).thenReturn(Optional.of(note1));

    Mockito.when(noteRepository.save(note2)).thenReturn(note2);
    Mockito.when(noteRepository.findById(note2.getUuid())).thenReturn(Optional.of(note2));
    noteService.addNote(note1, new User());
    noteService.addNote(note2, new User());

    Optional<Note> optionalNote1 = noteService.getNote(note1.getUuid());
    Optional<Note> optionalNote2 = noteService.getNote(note2.getUuid());

    assertThat(optionalNote1.isPresent()).isTrue();
    assertThat(optionalNote2.isPresent()).isTrue();
  }

  @Test
  public void AddNotes_WhenGettingAllNotes_ShouldGetAllNotes() {

    Note note1 = new Note("Nueva Nota");
    Note note2 = new Note("Nueva Nota");

    Mockito.when(noteRepository.findAll()).thenReturn(Arrays.asList(note1, note2));
    noteService.addNote(note1, new User());
    noteService.addNote(note2, new User());

    Mockito.when(noteRepository.save(note1)).thenReturn(note1);
    Mockito.when(noteRepository.save(note2)).thenReturn(note2);

    List<Note> notes = noteService.getAllNotes();

    assertThat(notes.size()).isEqualTo(2);
    assertThat(notes.containsAll(Arrays.asList(note1, note2))).isTrue();
  }

  @Test
  public void DeleteNote_WhenNoteExists_ShouldDeleteTheNote() {
    Note note = new Note("This is the new note");

    Mockito.when(noteRepository.findById(1L)).thenReturn(Optional.of(note));
    noteService.addNote(note, new User());

    Mockito.when(noteRepository.save(note)).thenReturn(note);
    Mockito.when(noteRepository.findAll()).thenReturn(Collections.singletonList(note));

    List<Note> notes = noteService.getAllNotes();
    assertThat(notes.size()).isEqualTo(1);
    assertThat(notes.contains(note)).isTrue();

    noteService.deleteNote(note.getUuid());

    Mockito.when(noteRepository.findAll()).thenReturn(Collections.emptyList());

    List<Note> noteList = noteService.getAllNotes();
    assertThat(noteList.size()).isEqualTo(0);
    assertThat(noteList.contains(note)).isFalse();

    assertThat(noteService.getAllNotes().contains(note)).isFalse();
  }
  
  @Test
  public void getOwner_WhenOwnerExists_ShouldReturnIt() {
    final Note note = new Note();
    final long id = 1;
    final User user = new User();
    final Permission p = new Permission(user, note, PermissionType.Owner);
    final List<Permission> permissions = new ArrayList<>();
    user.setUuid(id);
    permissions.add(p);
    note.setPermissions(permissions);
    final Optional<User> owner = noteService.getOwner(note);
    assertThat(owner.isPresent()).isTrue();
    assertThat(owner.get().getUuid()).isEqualTo(id);
  }
  
  @Test
  public void getOwner_WhenOwnerDoesNotExist_ShouldReturnEmptyOptional() {
    final Note note = new Note();
    final long id = 1;
    final User user = new User();
    final Permission p = new Permission(user, note, PermissionType.Viewer);
    final List<Permission> permissions = new ArrayList<>();
    user.setUuid(id);
    permissions.add(p);
    note.setPermissions(permissions);
    final Optional<User> owner = noteService.getOwner(note);
    assertThat(owner.isPresent()).isFalse();
  }
  
  @Test
  public void getUsersWithPermissions_WhenNoteHasPermissions_ShouldReturnThem() {
    final Note note = new Note();
    final User user1 = new User();
    final User user2 = new User();
    final List<Permission> permissions = new ArrayList<>();
    final List<User> userList = new ArrayList<>();
    userList.add(user1);
    userList.add(user2);
    permissions.add(new Permission(user1, note, PermissionType.Owner));
    permissions.add(new Permission(user2, note, PermissionType.Editor));
    note.setPermissions(permissions);
    final List<User> usersWithPermissions = noteService.getUsersWithPermissions(note);
    assertThat(usersWithPermissions.size()).isEqualTo(2);
    assertThat(usersWithPermissions).isEqualTo(userList);
  }
  
  @Test
  public void getUsersWithPermissions_WhenNoteHasNoPermissions_ShouldReturnEmptyList() {
    final List<User> usersWithPermissions = noteService.getUsersWithPermissions(new Note());
    assertThat(usersWithPermissions.size()).isEqualTo(0);
  }
  
  @Test
  public void getUsersWithEditOrOwner_WhenNoteHasPermissions_ShouldReturnThem() {
    final Note note = new Note();
    final User owner = new User();
    final User editor = new User();
    final User viewer = new User();
    final List<Permission> permissions = new ArrayList<>();
    permissions.add(new Permission(owner, note, PermissionType.Owner));
    permissions.add(new Permission(editor, note, PermissionType.Editor));
    permissions.add(new Permission(viewer, note, PermissionType.Viewer));
    note.setPermissions(permissions);
    final List<User> usersWithPermissions = noteService.getUsersWithEditOrOwner(note);
    assertThat(usersWithPermissions.size()).isEqualTo(2);
    assertThat(usersWithPermissions.contains(owner)).isEqualTo(true);
    assertThat(usersWithPermissions.contains(editor)).isEqualTo(true);
    assertThat(usersWithPermissions.contains(viewer)).isEqualTo(false);
  }
  
  @Test
  public void getUsersWithEditOrOwner_WhenNoteHasNoPermissions_ShouldReturnEmptyList() {
    final List<User> users = noteService.getUsersWithEditOrOwner(new Note());
    assertThat(users.size()).isEqualTo(0);
  }
  
  @Test
  public void addTags_ShouldAddTheTagsToNoteAndKeepOldOnes() {
    final Note note = new Note();
    List<String> oldTags = new ArrayList<>();
    oldTags.add("food");
    note.setTags(new ArrayList<>(oldTags));
    List<String> newTags = new ArrayList<>();
    newTags.add("fun");
    newTags.add("inspirational");
    newTags.add("diy");
    
    noteService.addTags(note, newTags);
    
    List<String> allTags = new ArrayList<>(oldTags);
    allTags.addAll(newTags);
    assertThat(note.getTags()).isEqualTo(allTags);
  }
}
