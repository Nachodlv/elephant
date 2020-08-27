package com.lab.elephant.controller;

import com.lab.elephant.model.Note;
import com.lab.elephant.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@CrossOrigin
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

  @GetMapping("{id}")
  public Note getNote(@PathVariable("id") long id) {
    Optional<Note> note = noteService.getNote(id);
    if (note.isPresent()) return note.get();
    throw new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Note Not Found");
  }
}
