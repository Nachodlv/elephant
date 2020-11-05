package com.lab.elephant.service;

import com.lab.elephant.model.Note;
import com.lab.elephant.model.Permission;
import com.lab.elephant.model.PermissionType;
import com.lab.elephant.model.User;
import com.lab.elephant.repository.NoteRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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

    when(noteRepository.save(note)).thenReturn(note);
    when(noteRepository.findById(note.getUuid())).thenReturn(Optional.of(note));
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

    when(noteRepository.save(note1)).thenReturn(note1);
    when(noteRepository.findById(note1.getUuid())).thenReturn(Optional.of(note1));

    when(noteRepository.save(note2)).thenReturn(note2);
    when(noteRepository.findById(note2.getUuid())).thenReturn(Optional.of(note2));
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

    when(noteRepository.findAll()).thenReturn(Arrays.asList(note1, note2));
    noteService.addNote(note1, new User());
    noteService.addNote(note2, new User());

    when(noteRepository.save(note1)).thenReturn(note1);
    when(noteRepository.save(note2)).thenReturn(note2);

    List<Note> notes = noteService.getAllNotes();

    assertThat(notes.size()).isEqualTo(2);
    assertThat(notes.containsAll(Arrays.asList(note1, note2))).isTrue();
  }

  @Test
  public void DeleteNote_WhenNoteExists_ShouldDeleteTheNote() {
    Note note = new Note("This is the new note");

    when(noteRepository.findById(1L)).thenReturn(Optional.of(note));
    noteService.addNote(note, new User());

    when(noteRepository.save(note)).thenReturn(note);
    when(noteRepository.findAll()).thenReturn(Collections.singletonList(note));

    List<Note> notes = noteService.getAllNotes();
    assertThat(notes.size()).isEqualTo(1);
    assertThat(notes.contains(note)).isTrue();

    noteService.deleteNote(note.getUuid());

    when(noteRepository.findAll()).thenReturn(Collections.emptyList());

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
  public void addTags_WhenNoteExists_ShouldReturnOptionalOfNoteWithOnlyTheNewTags() {
    final Note note = new Note();
    final long id = 1;
    List<String> oldTags = new ArrayList<>();
    oldTags.add("food");
    note.setTags(new ArrayList<>(oldTags));
    List<String> newTags = new ArrayList<>();
    newTags.add("fun");
    newTags.add("inspirational");
    newTags.add("diy");

    when(noteRepository.findById(id)).thenReturn(Optional.of(note));
    when(noteRepository.save(note)).thenReturn(note);
    final Optional<Note> optionalNote = noteService.addTags(id, newTags);

    assertThat(optionalNote.isPresent()).isTrue();
    assertThat(optionalNote.get().getTags()).isEqualTo(newTags);
    assertThat(optionalNote.get().getTags().containsAll(oldTags)).isFalse();
  }

  @Test
  public void addTags_WhenNoteDoesNotExist_ShouldReturnEmptyOptional() {
    final long id = 1;
    final List<String> tags = new ArrayList<>();
    final Optional<Note> optionalNote = noteService.addTags(id, tags);
    assertThat(optionalNote.isPresent()).isFalse();
  }

  @Test
  public void unlockNote_ShouldLockNote() {
    final Note note = new Note();
    note.setLocked(true);

    when(noteRepository.save(note)).thenReturn(note);
    final Optional<Note> optionalNote = noteService.unlockNote(note);

    assertThat(optionalNote.isPresent()).isTrue();
    assertThat(optionalNote.get().isLocked()).isFalse();
  }

  @Test
  public void setLocked_ShouldLockNoteAndSetLastLockedToActualTime() {
    final Note note = new Note();

    when(noteRepository.save(note)).thenReturn(note);

    final long actualTime = System.currentTimeMillis();
    final Optional<Note> optionalNote = noteService.setLocked(note);

    assertThat(optionalNote.isPresent()).isTrue();
    assertThat(optionalNote.get().isLocked()).isTrue();
    assertThat(optionalNote.get().getLastLocked()).isEqualTo(new Timestamp(actualTime));
  }

  @Test
  public void editNote_WhenEverythingIsOk_ShouldReturnOptionalOfEditedNote() {
    //this test does not test if the Note gets locked when edited because that is already tested on another test.
    final Note oldNote = new Note();
    final Note newNote = new Note();
    final long oldNoteId = 1;
    final String title = "Something";
    final String content = "new";
    oldNote.setUuid(oldNoteId);
    newNote.setTitle(title);
    newNote.setContent(content);
    when(noteRepository.findById(oldNoteId)).thenReturn(Optional.of(oldNote));

    when(noteRepository.save(oldNote)).thenReturn(oldNote);
    final Optional<Note> optionalNote = noteService.editNote(oldNoteId, newNote);

    assertThat(optionalNote.isPresent()).isTrue();
    final Note note = optionalNote.get();
    assertThat(note.getTitle()).isEqualTo(title);
    assertThat(note.getContent()).isEqualTo(content);
    assertThat(note.getUuid()).isEqualTo(oldNoteId);
  }

  @Test
  public void copyNote_WhenNoteExists_ReturnNewNoteCopied() {
    Timestamp ts = new Timestamp(new Date().getTime());
    Note note = new Note("Nueva Nota", "", ts);
    User user = new User();
    noteService.addNote(note, user);

    when(noteRepository.findById(note.getUuid())).thenReturn(Optional.of(note));
    when(noteRepository.save(note)).thenReturn(note);
    noteService.addNote(note, new User());

    Note noteCopied = new Note("Nueva Nota (Copia)", "", ts);
    when(noteRepository.findById(noteCopied.getUuid())).thenReturn(Optional.of(noteCopied));
    when(noteRepository.save(noteCopied)).thenReturn(noteCopied);
    when(noteRepository.findAll()).thenReturn(Arrays.asList(note, noteCopied));

    noteService.copyNote(note, user);

    Optional<Note> optionalNote = noteService.getNote(noteCopied.getUuid());

    assertThat(optionalNote.isPresent()).isTrue();
    assertThat(optionalNote.get().getTitle()).isEqualTo(noteCopied.getTitle());
    assertThat(noteService.getAllNotes().size()).isEqualTo(2);
  }

}
