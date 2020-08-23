package com.lab.elephant.controller;

import com.lab.elephant.model.Note;
import com.lab.elephant.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/note")
public class NoteController {

  private final NoteService noteService;

  public NoteController(NoteService noteService) {
    this.noteService = noteService;
  }

  @PostMapping("/new")
  public Note addNote(@RequestBody Note note) {
    if (note.getTitle().length() > 60)
      throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST, "Title is too long");
    return noteService.addNote(note);
  }

}
