package com.lab.elephant.service;

import com.lab.elephant.model.Note;
import com.lab.elephant.model.PermissionType;
import com.lab.elephant.model.User;
import com.lab.elephant.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class NoteServiceImpl implements NoteService {

  private final NoteRepository noteRepository;
  private final PermissionService permissionService;
  
  public NoteServiceImpl(NoteRepository noteRepository, PermissionService permissionService) {
    this.noteRepository = noteRepository;
    this.permissionService = permissionService;
  }

  @Override
  public Note addNote(Note note, User user) {
    note.setContent("");
    note.setCreated(new Timestamp(System.currentTimeMillis()));
    final Note savedNote = noteRepository.save(note);
    permissionService.addRelationShip(user, note, PermissionType.Owner);
    return savedNote;
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
  public void deleteNote(long id) {
    noteRepository.deleteById(id);
  }
}
