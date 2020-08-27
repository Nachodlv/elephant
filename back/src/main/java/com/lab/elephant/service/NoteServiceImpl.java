package com.lab.elephant.service;

import com.lab.elephant.model.Note;
import com.lab.elephant.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class NoteServiceImpl implements NoteService {

  private final NoteRepository noteRepository;

  public NoteServiceImpl(NoteRepository noteRepository) {
    this.noteRepository = noteRepository;
  }

  @Override
  public Note addNote(Note note) {
    note.setContent("");
    note.setCreated(new Timestamp(System.currentTimeMillis()));
    return noteRepository.save(note);
  }

  @Override
  public Optional<Note> getNote(long id) {
    return noteRepository.findById(id);
  }

  @Override
  public List<Note> getAllNotes() {
    return noteRepository.findAll();
  }

  @Override
  public boolean deleteNote(long id) {
    if (noteRepository.findById(id).isPresent()){
      noteRepository.deleteById(id);
      return true;
    }
    return false;
  }
}
