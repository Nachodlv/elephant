package com.lab.elephant.service;

import com.lab.elephant.model.Note;
import com.lab.elephant.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface NoteService {

  Note addNote(Note note, User user);

  Optional<Note> getNote(long id);

  List<Note> getAllNotes();

  void deleteNote(long id);

  Optional<User> getOwner(Note note);

  List<User> getUsersWithPermissions(Note note);

  List<User> getUsersWithEditOrOwner(Note note);

  Optional<Note> addTags(long id, List<String> tags);

  Optional<Note> editNote(long oldNoteId, Note note);

  Optional<Note> setLocked(Note note);

  Optional<Note> unlockNote(Note note);

  Note copyNote(Note note, User user);
}
