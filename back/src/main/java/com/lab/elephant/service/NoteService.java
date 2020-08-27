package com.lab.elephant.service;

import com.lab.elephant.model.Note;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface NoteService {

  Note addNote(Note note);

  Optional<Note> getNote(long id);

  List<Note> getAllNotes();

  boolean deleteNote(long id);
}
