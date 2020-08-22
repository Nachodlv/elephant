package com.lab.elephant.service;

import com.lab.elephant.model.Note;
import com.lab.elephant.repository.NoteRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class NoteServiceTest {

    @TestConfiguration
    static class CubeServiceImplTestContextConfiguration {
        @Autowired
        private NoteRepository noteRepository;

        @Bean
        public NoteService employeeService() {
            return new NoteServiceImpl(noteRepository);
        }
    }

    @Autowired
    private NoteService noteService;
    @MockBean
    private NoteRepository noteRepository;

    @Test
    public void AddNote_WhenAddingANewNote_ShouldReturnANewNote() {

        Timestamp created = new Timestamp(System.currentTimeMillis());
        Note note = new Note("Nueva Nota", "", created);

        Mockito.when(noteRepository.save(note)).thenReturn(note);
        Mockito.when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        noteService.addNote(note);

        Optional<Note> optionalNote = noteRepository.findById(1L);

        assertThat(optionalNote.isPresent()).isTrue();
        assertThat(optionalNote.get().getTitle()).isEqualTo(note.getTitle());
        assertThat(optionalNote.get().getContent()).isEqualTo(note.getContent());
        assertThat(optionalNote.get().getCreated()).isNotNull();
    }

    @Test
    public void AddNote_WhenGettingANewNote_ShouldGetTheNewNote() {

        Timestamp created = new Timestamp(System.currentTimeMillis());
        Note note1 = new Note("Nueva Nota", "", created);
        Note note2 = new Note("Nueva Nota", "", created);

        Mockito.when(noteRepository.save(note1)).thenReturn(note1);
        Mockito.when(noteRepository.findById(1L)).thenReturn(Optional.of(note1));

        Mockito.when(noteRepository.save(note2)).thenReturn(note2);
        Mockito.when(noteRepository.findById(2L)).thenReturn(Optional.of(note2));

        noteService.addNote(note1);
        noteService.addNote(note2);

        Optional<Note> optionalNote1 = noteService.getNote(1L);
        Optional<Note> optionalNote2 = noteService.getNote(2L);

        assertThat(optionalNote1.isPresent()).isTrue();
        assertThat(optionalNote2.isPresent()).isTrue();
    }

    @Test
    public void AddNotes_WhenGettingAllNotes_ShouldGetAllNotes() {

        Timestamp created = new Timestamp(System.currentTimeMillis());
        Note note1 = new Note("Nueva Nota", "", created);
        Note note2 = new Note("Nueva Nota", "", created);

        Mockito.when(noteRepository.findAll()).thenReturn(Arrays.asList(note1, note2));

        noteService.addNote(note1);
        noteService.addNote(note2);

        List<Note> notes = noteService.getAllNotes();

        assertThat(notes.size()).isEqualTo(2);
        assertThat(notes.containsAll(Arrays.asList(note1, note2))).isTrue();
    }

}
