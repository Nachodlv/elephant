package com.lab.elephant.controller;

import com.lab.elephant.model.Note;
import com.lab.elephant.model.User;
import com.lab.elephant.service.NoteService;
import com.lab.elephant.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(value = "/note")
public class NoteController {

  private final NoteService noteService;
  private final UserService userService;
  
  public NoteController(NoteService noteService, UserService userService) {
    this.noteService = noteService;
    this.userService = userService;
  }

  @PostMapping("/new")
  public Note addNote(@RequestBody Note note) {
    if (note.getTitle().length() > 60)
      throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST, "Title is too long");
  
    final String string = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    final Optional<User> user = userService.getByEmail(string);
    return noteService.addNote(note, user.get());
  }

  @GetMapping("{id}")
  public Note getNote(@PathVariable("id") long id) {
    Optional<Note> note = noteService.getNote(id);
    if (note.isPresent()) return note.get();
    throw new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Note Not Found");
  }

  @DeleteMapping(path = "/delete/{id}")
  public boolean deleteNote(@PathVariable("id") long id) {
    Optional<Note> optionalNote = noteService.getNote(id);
    if (optionalNote.isPresent()) {
      noteService.deleteNote(id);
      return true;
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note Not Found");
  }
}
