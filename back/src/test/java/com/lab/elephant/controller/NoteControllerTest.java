package com.lab.elephant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.elephant.model.Note;
import com.lab.elephant.service.NoteServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(NoteController.class)
public class NoteControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private NoteServiceImpl noteService;

  @Test
  public void addNewNote_WhenNoteCreated_ShouldReturnNewNote() throws Exception {
    Timestamp ts = new Timestamp(new Date().getTime());
    Note note = new Note("Nueva Nota", "", ts);
    Optional<Note> optionalNote = Optional.of(note);
    given(noteService.getNote(note.getUuid())).willReturn(optionalNote);
    final String noteJson = objectMapper.writeValueAsString(note);
    mvc.perform(post("/note/new").content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
  }

  @Test
  public void addNewNote_WhenTitleLengthIsMoreThan60_ShouldReturnBadRequest() throws Exception {
    Timestamp ts = new Timestamp(new Date().getTime());
    Note note = new Note("Este titulo contiene mas de 60 letras, por lo cual debe tirar un error",
            "", ts);

    final String noteJson = objectMapper.writeValueAsString(note);

    mvc.perform(post("/note/new").content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());
  }

  @Test
  public void deleteNote_WhenNoteExists_ShouldDeleteIt() throws Exception {
    Note note = new Note("This is the new note");

    noteService.addNote(note);

    given(noteService.getNote(1L)).willReturn(Optional.of(note));
    final String noteJson = objectMapper.writeValueAsString(note);

    mvc.perform(delete("/note/delete/1").content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
  }

  @Test
  public void deleteNote_WhenNoteNotExists_ShouldReturnNotFound() throws Exception {
    given(noteService.getNote(1L)).willReturn(Optional.empty());

    mvc.perform(delete("/note/delete/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound());

  }

}
