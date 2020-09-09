package com.lab.elephant.service;

import com.lab.elephant.model.Note;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class NoteServiceTest {

  @TestConfiguration
  static class CubeServiceImplTestContextConfiguration {
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
  //todo add test for noteService.findOwner();
}
