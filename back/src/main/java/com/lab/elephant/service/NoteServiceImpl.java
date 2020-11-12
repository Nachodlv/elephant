package com.lab.elephant.service;

import com.lab.elephant.model.Note;
import com.lab.elephant.model.Permission;
import com.lab.elephant.model.PermissionType;
import com.lab.elephant.model.User;
import com.lab.elephant.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
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
    permissionService.addRelationship(user, note, PermissionType.Owner);
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

  @Override
  public Optional<User> getOwner(Note note) {
    for (Permission p : note.getPermissions()) {
      if (p.getType().equals(PermissionType.Owner)) return Optional.of(p.getUser());
    }
    return Optional.empty();
  }

  @Override
  public List<User> getUsersWithPermissions(Note note) {
    List<User> users = new ArrayList<>();
    for (Permission p : note.getPermissions())
      users.add(p.getUser());
    return users;
  }

  @Override
  public List<User> getUsersWithEditOrOwner(Note note) {
    List<User> users = new ArrayList<>();
    for (Permission p : note.getPermissions())
      if (p.getType() != PermissionType.Viewer)
        users.add(p.getUser());
    return users;
  }

  @Override
  public Optional<Note> addTags(long id, List<String> tags) {
    final Optional<Note> optionalNote = getNote(id);
    if (optionalNote.isPresent()) {
      Note note = optionalNote.get();
      note.setTags(tags);
      return Optional.of(noteRepository.save(note));
    }
    return Optional.empty();
  }

  @Override
  public Optional<Note> editNote(long oldNoteId, Note newNote) {
    final Optional<Note> optionalNote = getNote(oldNoteId);
    if (!optionalNote.isPresent()) return Optional.empty();
    final Note note = optionalNote.get();
    note.setTitle(newNote.getTitle());
    note.setContent(newNote.getContent());
    return setLocked(note);
  }

  @Override
  public Optional<Note> setLocked(Note note) {
    note.setLocked(true);
    note.setLastLocked(new Timestamp(System.currentTimeMillis()));
    return Optional.of(noteRepository.save(note));
  }

  @Override
  public Optional<Note> unlockNote(Note note) {
    note.setLocked(false);
    return Optional.of(noteRepository.save(note));
  }

  @Override
  public Note copyNote(Note oldNote, User user) {
    Note newNote = new Note(oldNote.getTitle() + " (Copia)", oldNote.getContent(), new Timestamp(System.currentTimeMillis()));
    if (!oldNote.getTags().isEmpty()) {
      List<String> tags = (List<String>) new ArrayList<>(oldNote.getTags()).clone();
      newNote.setTags(tags);
    }
    final Note savedNote = noteRepository.save(newNote);
    permissionService.addRelationship(user, savedNote, PermissionType.Owner);
    return savedNote;
  }
}
