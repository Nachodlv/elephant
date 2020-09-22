package com.lab.elephant.controller;

import com.lab.elephant.model.Note;
import com.lab.elephant.model.User;
import com.lab.elephant.service.NoteService;
import com.lab.elephant.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
    final String string = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    final User user = userService.getByEmail(string).get();
    Optional<Note> noteOptional = noteService.getNote(id);
    if (noteOptional.isPresent()) {
      final Note note = noteOptional.get();
      final List<User> usersWithPermissions = noteService.getUsersWithPermissions(note);
      if (usersWithPermissions.contains(user))
        return note;
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User cannot view this note");
    }
    throw new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Note Not Found");
  }

  @DeleteMapping(path = "/delete/{id}")
  public boolean deleteNote(@PathVariable("id") long id) {
    Optional<Note> optionalNote = noteService.getNote(id);
    final String string = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    final User user = userService.getByEmail(string).get();
    
    if (optionalNote.isPresent()) {
      final Optional<User> owner = noteService.getOwner(optionalNote.get());
      if (!owner.isPresent())
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Note has no owner");
      if (user.getUuid() != owner.get().getUuid())
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User can't delete this note");
      noteService.deleteNote(id);
      return true;
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note Not Found");
  }
  
  @PutMapping("/addTags/{id}")
  public void addTags(@PathVariable("id") long id, @RequestBody List<String> tags) {
    Optional<Note> optionalNote = noteService.getNote(id);
    final String string = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    final User user = userService.getByEmail(string).get();
    
    if (!optionalNote.isPresent())
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note Not Found");
    final Note note = optionalNote.get();
    final List<User> usersWithEditOrOwner = noteService.getUsersWithEditOrOwner(note);
    if (!usersWithEditOrOwner.contains(user))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User cannot add Tags to this note");
    noteService.addTags(note, tags);
  }
}
