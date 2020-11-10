package com.lab.elephant.controller;

import com.lab.elephant.model.*;
import com.lab.elephant.service.NoteService;
import com.lab.elephant.service.PermissionService;
import com.lab.elephant.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

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
    
    final String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    final Optional<User> requestUser = userService.getByEmail(email);
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
    final Note note = getNote(noteId);
    final User user = getUser();
    final List<User> usersWithPermissions = noteService.getUsersWithPermissions(note);
    if (!usersWithPermissions.contains(user))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User has no Permission");
    return permissionService.getPermissionTypeBetween(user, note).get().toString();
  }
  
  @GetMapping(path = "/allPermissions/{noteId}")
  public List<PermissionDTO> getAllPermissions(@PathVariable long noteId) {
    final Note note = getNote(noteId);
    final User user = getUser();
    checkOwnership(user, note);
    final List<PermissionDTO> list = new ArrayList<>();
    for (Permission p : note.getPermissions())
      if (!p.getType().equals(PermissionType.Owner))
        list.add(new PermissionDTO(p));
    
    return list;
  }
  
  @PutMapping(path = "editPermissions/{noteId}")
  public void editPermissions(@PathVariable long noteId, @RequestBody EditPermissionDTO dto) {
    final Note note = getNote(noteId);
    final User user = getUser();
    checkOwnership(user, note);
  
    final List<PermissionDTO> permissionDTOList = dto.getList();
    final Map<User, String> map = new HashMap<>();
  
    for (PermissionDTO e : permissionDTOList) {
      final Optional<User> optionalUser = userService.getByEmail(e.getEmail());
      if (!optionalUser.isPresent())
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email doesn't exist");
      final User u = optionalUser.get();
      final Optional<PermissionType> optionalP = permissionService.getPermissionTypeBetween(u, note);
      if (!optionalP.isPresent())
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has no Permissions with Note");
      if (optionalP.get().equals(PermissionType.Owner))
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has Owner permission that can't be changed");
      if (!e.getType().equals("delete")) {
        try {
          final PermissionType permissionType = PermissionType.valueOf(e.getType());
          if (permissionType.equals(PermissionType.Owner))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The Owner permission can't be assigned to a new user");
        } catch (IllegalArgumentException ignored) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission Type Not Found");
        }
      }
      map.put(u, e.getType());
    }
    map.forEach((u, s) -> permissionService.editRelationship(u, note, s));
  }

  @PutMapping("/changePin/{noteId}")
  public boolean changePin(@PathVariable long noteId) {
    final Note note = getNote(noteId);
    final User user = getUser();
    if (!noteService.getUsersWithPermissions(note).contains(user))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User has no Permissions with Note");
    return permissionService.changePin(user, note);
  }
  
  // Private Methods
  private Note getNote(long noteId) {
    final Optional<Note> optionalNote = noteService.getNote(noteId);
    if (!optionalNote.isPresent())
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note Not Found");
    return optionalNote.get();
  }
  
  private User getUser() {
    final String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userService.getByEmail(email).get();
  }
  
  private void checkOwnership(User user, Note note) {
    final Optional<User> owner = noteService.getOwner(note);
    if (!owner.isPresent())
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Note has no owner");
  
    if (owner.get().getUuid() != user.getUuid())
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not the Note owner");
  }
}
