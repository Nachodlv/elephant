package com.lab.elephant.controller;

import com.lab.elephant.model.Note;
import com.lab.elephant.model.PermissionType;
import com.lab.elephant.model.ShareNoteDTO;
import com.lab.elephant.model.User;
import com.lab.elephant.service.NoteService;
import com.lab.elephant.service.PermissionService;
import com.lab.elephant.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping
public class PermissionController {
  
  private final UserService userService;
  private final NoteService noteService;
  private final PermissionService permissionService;
  public PermissionController(UserService userService, NoteService noteService, PermissionService permissionService) {
    this.userService = userService;
    this.noteService = noteService;
    this.permissionService = permissionService;
  }
  
  @PutMapping(path = "{noteId}/permission/add")
  public void add(@RequestBody ShareNoteDTO dto, @PathVariable long noteId) {
    final PermissionType permission;
    final String permissionType = dto.getPermissionType();
    final String userEmail = dto.getEmail();
    try {
      permission = PermissionType.valueOf(permissionType);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission Type Not Found");
    }
    final Optional<User> oUser = userService.getByEmail(userEmail);
    if (!oUser.isPresent())
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
    final Optional<Note> oNote = noteService.getNote(noteId);
    if (!oNote.isPresent())
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note Not Found");
    
    final User user = oUser.get();
    final Note note = oNote.get();
    
    final String string = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    final Optional<User> requestUser = userService.getByEmail(string);
    final Optional<User> noteOwner = noteService.getOwner(note);
    if (!noteOwner.isPresent())
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Note has no owner");
    if (requestUser.get().getUuid() != noteOwner.get().getUuid())
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User can't modify this note");
    final List<User> usersWithPermissions = noteService.getUsersWithPermissions(note);
    if (usersWithPermissions.contains(user))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User already has a permission over this note");
    permissionService.addRelationship(user, note, permission);
  }
  
  @GetMapping(path = "{noteId}/permission")
  public String getPermission(@PathVariable long noteId) {
    final Optional<Note> optionalNote = noteService.getNote(noteId);
    final String string = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    final User user = userService.getByEmail(string).get();
    if (!optionalNote.isPresent())
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note Not Found");
    Note note = optionalNote.get();
    final List<User> usersWithPermissions = noteService.getUsersWithPermissions(note);
    if (!usersWithPermissions.contains(user))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User has no Permission");
    return permissionService.getPermissionBetween(user, note).get().toString();
  }
}