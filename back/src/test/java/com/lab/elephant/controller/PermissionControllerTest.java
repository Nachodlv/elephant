package com.lab.elephant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.elephant.model.*;
import com.lab.elephant.security.UserDetailsServiceImpl;
import com.lab.elephant.service.BlackListedTokenServiceImpl;
import com.lab.elephant.service.NoteService;
import com.lab.elephant.service.PermissionService;
import com.lab.elephant.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(PermissionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PermissionControllerTest {
  
  @Autowired
  private MockMvc mvc;
  @MockBean
  private PermissionService permissionService;
  @MockBean
  private UserService userService;
  @MockBean
  private NoteService noteService;
  // All of the MockBeans below
  // are not used but are necessary for the tests to run.
  @MockBean
  private UserDetailsServiceImpl userDetailsService;
  @MockBean
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  @MockBean
  private BlackListedTokenServiceImpl blackListedTokenService;
  @Test
  public void addPermission_WithEverythingOk_ShouldReturn_200() throws Exception {
    //creating user that made the note.
    final User owner = new User();
    final Note note = new Note("The way of kings");
    final long noteId = 1;
    note.setUuid(noteId);
    final List<Permission> ownerPermissions = new ArrayList<>();
    final List<Permission> notePermissions = new ArrayList<>();
    final Permission p = new Permission(owner, note, PermissionType.Owner);
    ownerPermissions.add(p);
    notePermissions.add(p);
    owner.setPermissions(ownerPermissions);
    note.setPermissions(notePermissions);
    //creating other objects for sharing
    final User friend = new User();
    owner.setUuid(1);
    friend.setUuid(2);
    final String email = "john@elephant.com";
    friend.setEmail(email);
    PermissionType permissionType = PermissionType.Viewer;
    //this is to mock the logged in user
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("owner");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("owner")).willReturn(Optional.of(owner));
    given(userService.getByEmail(email)).willReturn(Optional.of(friend));
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getOwner(note)).willReturn(Optional.of(owner));

    ObjectMapper o = new ObjectMapper();
    String json = o.writeValueAsString(new ShareNoteDTO(email, permissionType.toString()));
    mvc.perform(put("/" + noteId + "/permission/add")
            .contentType("application/json")
            .content(json))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
  }
  
  @Test
  public void addPermission_WithInvalidPermissionType_ShouldReturn_404() throws Exception {
    //creating user that made the note.
    final User owner = new User();
    final Note note = new Note("The way of kings");
    final long noteId = 1;
    note.setUuid(noteId);
    final List<Permission> ownerPermissions = new ArrayList<>();
    final List<Permission> notePermissions = new ArrayList<>();
    final Permission p = new Permission(owner, note, PermissionType.Owner);
    ownerPermissions.add(p);
    notePermissions.add(p);
    owner.setPermissions(ownerPermissions);
    note.setPermissions(notePermissions);
    //creating other objects for sharing
    final User friend = new User();
    owner.setUuid(1);
    friend.setUuid(2);
    final String email = "john@elephant.com";
    friend.setEmail(email);
    //this is to mock the logged in user
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("owner");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("owner")).willReturn(Optional.of(owner));
    given(userService.getByEmail(email)).willReturn(Optional.of(friend));
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getOwner(note)).willReturn(Optional.of(owner));
  
    ObjectMapper o = new ObjectMapper();
    final String invalidPermissionType = "invalidPermissionType";
    String json = o.writeValueAsString(new ShareNoteDTO(email, invalidPermissionType));
    mvc.perform(put("/" + noteId + "/permission/add")
            .contentType("application/json")
            .content(json))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound())
            .andExpect(status().reason("Permission Type Not Found"));
  }
  
  @Test
  public void addPermission_WithInvalidUserEmail_ShouldReturn_404() throws Exception {
    //creating user that made the note.
    final User owner = new User();
    final Note note = new Note("The way of kings");
    final long noteId = 1;
    note.setUuid(noteId);
    final List<Permission> ownerPermissions = new ArrayList<>();
    final List<Permission> notePermissions = new ArrayList<>();
    final Permission p = new Permission(owner, note, PermissionType.Owner);
    ownerPermissions.add(p);
    notePermissions.add(p);
    owner.setPermissions(ownerPermissions);
    note.setPermissions(notePermissions);
    //creating other objects for sharing
    final String email = "john@elephant.com";
    PermissionType permissionType = PermissionType.Viewer;
    //this is to mock the logged in user
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("owner");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("owner")).willReturn(Optional.of(owner));
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getOwner(note)).willReturn(Optional.of(owner));
  
    ObjectMapper o = new ObjectMapper();
    String json = o.writeValueAsString(new ShareNoteDTO(email, permissionType.toString()));
    mvc.perform(put("/" + noteId + "/permission/add")
            .contentType("application/json")
            .content(json))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound())
            .andExpect(status().reason("User Not Found"));
  }
  
  @Test
  public void addPermission_WithInvalidNoteId_ShouldReturn_404() throws Exception {
    //creating user that made the note.
    final User owner = new User();
    final Note note = new Note("The way of kings");
    final long noteId = 1;
    final List<Permission> ownerPermissions = new ArrayList<>();
    final List<Permission> notePermissions = new ArrayList<>();
    final Permission p = new Permission(owner, note, PermissionType.Owner);
    ownerPermissions.add(p);
    notePermissions.add(p);
    owner.setPermissions(ownerPermissions);
    note.setPermissions(notePermissions);
    //creating other objects for sharing
    final User friend = new User();
    owner.setUuid(1);
    friend.setUuid(2);
    final String email = "john@elephant.com";
    friend.setEmail(email);
    PermissionType permissionType = PermissionType.Viewer;
    //this is to mock the logged in user
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("owner");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("owner")).willReturn(Optional.of(owner));
    given(userService.getByEmail(email)).willReturn(Optional.of(friend));
    given(noteService.getOwner(note)).willReturn(Optional.of(owner));
  
    ObjectMapper o = new ObjectMapper();
    String json = o.writeValueAsString(new ShareNoteDTO(email, permissionType.toString()));
    mvc.perform(put("/" + noteId + "/permission/add")
            .contentType("application/json")
            .content(json))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound())
            .andExpect(status().reason("Note Not Found"));
  }
  
  @Test
  public void addPermission_WithNoteWithoutOwner_ShouldReturn_500() throws Exception {
    //creating user that made the note.
    final User owner = new User();
    final Note note = new Note("The way of kings");
    final long noteId = 1;
    note.setUuid(noteId);
    //creating other objects for sharing
    final User friend = new User();
    owner.setUuid(1);
    friend.setUuid(2);
    final String email = "john@elephant.com";
    friend.setEmail(email);
    PermissionType permissionType = PermissionType.Viewer;
    //this is to mock the logged in user
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("owner");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("owner")).willReturn(Optional.of(owner));
    given(userService.getByEmail(email)).willReturn(Optional.of(friend));
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
  
    ObjectMapper o = new ObjectMapper();
    String json = o.writeValueAsString(new ShareNoteDTO(email, permissionType.toString()));
    mvc.perform(put("/" + noteId + "/permission/add")
            .contentType("application/json")
            .content(json))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isInternalServerError())
            .andExpect(status().reason("Note has no owner"));
  }
  
  @Test
  public void addPermission_WithAuthenticatedUserThatIsNotTheNoteOwner_ShouldReturn_401() throws Exception {
    //creating user that made the note.
    final User owner = new User();
    final Note note = new Note("The way of kings");
    final long noteId = 1;
    note.setUuid(noteId);
    final List<Permission> ownerPermissions = new ArrayList<>();
    final List<Permission> notePermissions = new ArrayList<>();
    final Permission p = new Permission(owner, note, PermissionType.Owner);
    ownerPermissions.add(p);
    notePermissions.add(p);
    owner.setPermissions(ownerPermissions);
    note.setPermissions(notePermissions);
    //creating other objects for sharing
    final User friend = new User();
    final String email = "john@elephant.com";
    owner.setUuid(1);
    friend.setUuid(2);
    friend.setEmail(email);
    PermissionType permissionType = PermissionType.Viewer;
    //this is to mock the logged in user
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("notOwner");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("notOwner")).willReturn(Optional.of(friend));
    given(userService.getByEmail(email)).willReturn(Optional.of(friend));
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getOwner(note)).willReturn(Optional.of(owner));
  
    ObjectMapper o = new ObjectMapper();
    String json = o.writeValueAsString(new ShareNoteDTO(email, permissionType.toString()));
    mvc.perform(put("/" + noteId + "/permission/add")
            .contentType("application/json")
            .content(json))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isUnauthorized())
            .andExpect(status().reason("User can't modify this note"));
  }
  
  @Test
  public void addPermission_WithAuthenticatedUserThatAlreadyHasPermission_ShouldReturn_403() throws Exception {
    //creating user that made the note.
    final User owner = new User();
    final Note note = new Note("The way of kings");
    final long noteId = 1;
    note.setUuid(noteId);
    final List<Permission> ownerPermissions = new ArrayList<>();
    final List<Permission> notePermissions = new ArrayList<>();
    final Permission p = new Permission(owner, note, PermissionType.Owner);
    ownerPermissions.add(p);
    notePermissions.add(p);
    owner.setPermissions(ownerPermissions);
    note.setPermissions(notePermissions);
    //creating other objects for sharing
    final User friend = new User();
    owner.setUuid(1);
    friend.setUuid(2);
    final String email = "john@elephant.com";
    friend.setEmail(email);
    PermissionType permissionType = PermissionType.Viewer;
    
    final List<User> permissionUsers = new ArrayList<>();
    permissionUsers.add(friend);
    //this is to mock the logged in user
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("owner");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("owner")).willReturn(Optional.of(owner));
    given(userService.getByEmail(email)).willReturn(Optional.of(friend));
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getOwner(note)).willReturn(Optional.of(owner));
    given(noteService.getUsersWithPermissions(note)).willReturn(permissionUsers);
    
    ObjectMapper o = new ObjectMapper();
    String json = o.writeValueAsString(new ShareNoteDTO(email, permissionType.toString()));
    mvc.perform(put("/" + noteId + "/permission/add")
            .contentType("application/json")
            .content(json))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isForbidden())
            .andExpect(status().reason("User already has a permission over this note"));
  }
}
