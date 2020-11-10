package com.lab.elephant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.elephant.model.*;
import com.lab.elephant.security.UserDetailsServiceImpl;
import com.lab.elephant.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
  private UserServiceImpl userService;
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
    final String ownerEmail = mockUserAuthentication();
    given(userService.getByEmail(ownerEmail)).willReturn(Optional.of(owner));
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
    final String ownerEmail = mockUserAuthentication();
    given(userService.getByEmail(ownerEmail)).willReturn(Optional.of(owner));
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
    final String ownerEmail = mockUserAuthentication();
    given(userService.getByEmail(ownerEmail)).willReturn(Optional.of(owner));
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
    final String ownerEmail = mockUserAuthentication();
    given(userService.getByEmail(ownerEmail)).willReturn(Optional.of(owner));
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
    final String ownerEmail = mockUserAuthentication();
    given(userService.getByEmail(ownerEmail)).willReturn(Optional.of(owner));
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
    final String notOwner = mockUserAuthentication();
    given(userService.getByEmail(notOwner)).willReturn(Optional.of(friend));
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
    final String ownerEmail = mockUserAuthentication();
    given(userService.getByEmail(ownerEmail)).willReturn(Optional.of(owner));
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
  
  @Test
  public void getPermission_WithEverythingOk_ShouldReturn200AndThePermissionType() throws Exception {
    final long noteId = 1;
    final Note note = new Note();
    final User user = new User();
    final List<User> users = new ArrayList<>();
    final PermissionType permissionType = PermissionType.Viewer;
    users.add(user);
    final String email = mockUserAuthentication();
    given(userService.getByEmail(email)).willReturn(Optional.of(user));
    
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getUsersWithPermissions(note)).willReturn(users);
    given(permissionService.getPermissionTypeBetween(user, note)).willReturn(Optional.of(permissionType));
    
    MvcResult result = mvc.perform(get("/" + noteId + "/permission"))
            .andExpect(status().isOk()).andReturn();
    String content = result.getResponse().getContentAsString();
    assertThat(content).isEqualTo(permissionType.toString());
  }
  
  @Test
  public void getPermission_WithIdThatDoesNotExist_ShouldReturn404() throws Exception {
    final long noteId = 1;
    final Note note = new Note();
    final User user = new User();
    final List<User> users = new ArrayList<>();
    final PermissionType permissionType = PermissionType.Viewer;
    users.add(user);
    final String email = mockUserAuthentication();
    given(userService.getByEmail(email)).willReturn(Optional.of(user));
  
    given(noteService.getUsersWithPermissions(note)).willReturn(users);
    given(permissionService.getPermissionTypeBetween(user, note)).willReturn(Optional.of(permissionType));
  
    mvc.perform(get("/" + noteId + "/permission"))
            .andExpect(status().isNotFound())
            .andExpect(status().reason("Note Not Found"));
  }
  
  @Test
  public void getPermission_WhenUserDoesntHaveAPermission_ShouldReturn401() throws Exception {
    final long noteId = 1;
    final Note note = new Note();
    final User user = new User();
    final PermissionType permissionType = PermissionType.Viewer;
    final String email = mockUserAuthentication();
    given(userService.getByEmail(email)).willReturn(Optional.of(user));
  
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(permissionService.getPermissionTypeBetween(user, note)).willReturn(Optional.of(permissionType));
  
    mvc.perform(get("/" + noteId + "/permission"))
            .andExpect(status().isUnauthorized())
            .andExpect(status().reason("User has no Permission"));
  }
  
  @Test
  public void allPermissions_WhenEverythingIsOk_ShouldReturn200() throws Exception {
    final long noteId = 1;
    final Note note = new Note();
    final User user = new User();
    final List<Permission> permissions = new ArrayList<>();
    permissions.add(new Permission(user, note, PermissionType.Editor));
    note.setPermissions(permissions);
    
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    final String email = mockUserAuthentication();
    given(userService.getByEmail(email)).willReturn(Optional.of(user));
    given(noteService.getOwner(note)).willReturn(Optional.of(user));
  
    final MvcResult result = mvc.perform(get("/allPermissions/" + noteId))
            .andExpect(status().isOk()).andReturn();
    final String contentAsString = result.getResponse().getContentAsString();
  
    List<PermissionDTO> permissionDTOS = new ArrayList<>();
    permissions.forEach(e -> permissionDTOS.add(new PermissionDTO(e)));
    
    final String s = new ObjectMapper().writeValueAsString(permissionDTOS);
    assertThat(contentAsString).isEqualTo(s);
  }

  @Test
  public void allPermissions_WhenNoteDoesNotExist_ShouldReturn404() throws Exception {
    final long noteId = 1;
    mvc.perform(get("/allPermissions/" + noteId))
            .andExpect(status().isNotFound())
            .andExpect(status().reason("Note Not Found"));
  }
  
  @Test
  public void allPermissions_WhenNoteHasNoOwner_ShouldReturn500() throws Exception {
    final long noteId = 1;
    final String email = mockUserAuthentication();
    given(noteService.getNote(noteId)).willReturn(Optional.of(new Note()));
    given(userService.getByEmail(email)).willReturn(Optional.of(new User()));
    mvc.perform(get("/allPermissions/" + noteId))
            .andExpect(status().isInternalServerError())
            .andExpect(status().reason("Note has no owner"));
  }
  
  @Test
  public void allPermissions_WhenUserIsNotOwner_ShouldReturn401() throws Exception {
    final long noteId = 1;
    final User user = new User();
    final Note note = new Note();
    final String email = mockUserAuthentication();
    user.setUuid(2);
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(userService.getByEmail(email)).willReturn(Optional.of(user));
    given(noteService.getOwner(note)).willReturn(Optional.of(new User()));
    mvc.perform(get("/allPermissions/" + noteId))
            .andExpect(status().isUnauthorized())
            .andExpect(status().reason("User is not the Note owner"));
  }
  
  @Test
  public void editPermissions_WhenEverythingIsOk_ShouldReturn200() throws Exception {
    final long noteId = 1;
    final Note note = new Note();
    final User owner = new User();
  
    final String email1 = "a@a.com";
    final String email2 = "b@b.com";

    final User user1 = new User("user1", "one", email1, "asd");
    final User user2 = new User("user2", "two", email2, "asd");
    
    final Permission p1 = new Permission(user1, note, PermissionType.Editor);
    final Permission p2 = new Permission(user2, note, PermissionType.Viewer);
    
    final List<PermissionDTO> list = new ArrayList<>();
    list.add(new PermissionDTO(email1, "Viewer"));
    list.add(new PermissionDTO(email2, "Editor"));
    
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getOwner(note)).willReturn(Optional.of(owner));
    final String ownerEmail = mockUserAuthentication();
    given(userService.getByEmail(ownerEmail)).willReturn(Optional.of(owner));
    
    given(userService.getByEmail(email1)).willReturn(Optional.of(user1));
    given(userService.getByEmail(email2)).willReturn(Optional.of(user2));
    
    given(permissionService.getPermissionTypeBetween(user1, note)).willReturn(Optional.of(p1.getType()));
    given(permissionService.getPermissionTypeBetween(user2, note)).willReturn(Optional.of(p2.getType()));
    
    final String json = new ObjectMapper().writeValueAsString(new EditPermissionDTO(list));
    
    mvc.perform(put("/editPermissions/" + noteId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk());
  }
  
  @Test
  public void editPermissions_WhenNoteDoesNotExist_ShouldReturn404() throws Exception {
    final long noteId = 1;
    final String json = new ObjectMapper().writeValueAsString(new EditPermissionDTO());
    
    mvc.perform(put("/editPermissions/" + noteId)
            .contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isNotFound())
            .andExpect(status().reason("Note Not Found"));
  }
  
  @Test
  public void editPermissions_WhenNoteHasNoOwner_ShouldReturn500() throws Exception {
    final long noteId = 1;
    final String email = mockUserAuthentication();
    given(noteService.getNote(noteId)).willReturn(Optional.of(new Note()));
    given(userService.getByEmail(email)).willReturn(Optional.of(new User()));
    
    final String json = new ObjectMapper().writeValueAsString(new EditPermissionDTO());
    mvc.perform(put("/editPermissions/" + noteId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isInternalServerError())
            .andExpect(status().reason("Note has no owner"));
  }
  
  @Test
  public void editPermissions_WhenUserIsNotOwner_ShouldReturn401() throws Exception {
    final long noteId = 1;
    final User user = new User();
    final Note note = new Note();
    final String email = mockUserAuthentication();
    user.setUuid(2);
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(userService.getByEmail(email)).willReturn(Optional.of(user));
    given(noteService.getOwner(note)).willReturn(Optional.of(new User()));
  
    final String json = new ObjectMapper().writeValueAsString(new EditPermissionDTO());
  
    mvc.perform(put("/editPermissions/" + noteId)
            .contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isUnauthorized())
            .andExpect(status().reason("User is not the Note owner"));
  }
  
  @Test
  public void editPermissions_WhenOneUserDoesNotExists_ShouldReturn400() throws Exception {
    final long noteId = 1;
    final Note note = new Note();
    final User owner = new User();
    
    final String email1 = "a@a.com";
    final String email2 = "b@b.com";
    
    final List<PermissionDTO> list = new ArrayList<>();
    list.add(new PermissionDTO(email1, "Viewer"));
    list.add(new PermissionDTO(email2, "Editor"));
    
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getOwner(note)).willReturn(Optional.of(owner));
    final String ownerEmail = mockUserAuthentication();
    given(userService.getByEmail(ownerEmail)).willReturn(Optional.of(owner));
    
    final String json = new ObjectMapper().writeValueAsString(new EditPermissionDTO(list));
    
    mvc.perform(put("/editPermissions/" + noteId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason("Email doesn't exist"));
    
  }
  
  @Test
  public void editPermissions_WhenOneUserHasNoPermissionsWithNote_ShouldReturn400() throws Exception {
    final long noteId = 1;
    final Note note = new Note();
    final User owner = new User();
    
    final String email1 = "a@a.com";
    final String email2 = "b@b.com";
    
    final User user1 = new User("user1", "one", email1, "asd");
    final User user2 = new User("user2", "two", email2, "asd");
    
    
    final List<PermissionDTO> list = new ArrayList<>();
    list.add(new PermissionDTO(email1, "Viewer"));
    list.add(new PermissionDTO(email2, "Editor"));
    
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getOwner(note)).willReturn(Optional.of(owner));
    final String ownerEmail = mockUserAuthentication();
    given(userService.getByEmail(ownerEmail)).willReturn(Optional.of(owner));
    
    given(userService.getByEmail(email1)).willReturn(Optional.of(user1));
    given(userService.getByEmail(email2)).willReturn(Optional.of(user2));
    
    final String json = new ObjectMapper().writeValueAsString(new EditPermissionDTO(list));
    
    mvc.perform(put("/editPermissions/" + noteId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason("User has no Permissions with Note"));
    
  }
  
  @Test
  public void editPermissions_WhenAPermissionIsEditedToOwner_ShouldReturn401() throws Exception {
    final long noteId = 1;
    final Note note = new Note();
    final User owner = new User();
    
    final String email1 = "a@a.com";
    final String email2 = "b@b.com";
    
    final User user1 = new User("user1", "one", email1, "asd");
    final User user2 = new User("user2", "two", email2, "asd");
    
    final Permission p1 = new Permission(user1, note, PermissionType.Editor);
    final Permission p2 = new Permission(user2, note, PermissionType.Viewer);
    
    final List<PermissionDTO> list = new ArrayList<>();
    list.add(new PermissionDTO(email1, "Viewer"));
    list.add(new PermissionDTO(email2, "Owner"));
    
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getOwner(note)).willReturn(Optional.of(owner));
    final String ownerEmail = mockUserAuthentication();
    given(userService.getByEmail(ownerEmail)).willReturn(Optional.of(owner));
    
    given(userService.getByEmail(email1)).willReturn(Optional.of(user1));
    given(userService.getByEmail(email2)).willReturn(Optional.of(user2));
    
    given(permissionService.getPermissionTypeBetween(user1, note)).willReturn(Optional.of(p1.getType()));
    given(permissionService.getPermissionTypeBetween(user2, note)).willReturn(Optional.of(p2.getType()));
    
    final String json = new ObjectMapper().writeValueAsString(new EditPermissionDTO(list));
    
    mvc.perform(put("/editPermissions/" + noteId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isUnauthorized())
            .andExpect(status().reason("The Owner permission can't be assigned to a new user"));
  }
  
  @Test
  public void editPermissions_WhenAPermissionIsEditedToInvalidValue_ShouldReturn404() throws Exception {
    final long noteId = 1;
    final Note note = new Note();
    final User owner = new User();
    
    final String email1 = "a@a.com";
    final String email2 = "b@b.com";
    
    final User user1 = new User("user1", "one", email1, "asd");
    final User user2 = new User("user2", "two", email2, "asd");
    
    final Permission p1 = new Permission(user1, note, PermissionType.Editor);
    final Permission p2 = new Permission(user2, note, PermissionType.Viewer);
    
    final List<PermissionDTO> list = new ArrayList<>();
    list.add(new PermissionDTO(email1, "asdasd"));
    list.add(new PermissionDTO(email2, "Editor"));
    
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getOwner(note)).willReturn(Optional.of(owner));
    final String ownerEmail = mockUserAuthentication();
    given(userService.getByEmail(ownerEmail)).willReturn(Optional.of(owner));
    
    given(userService.getByEmail(email1)).willReturn(Optional.of(user1));
    given(userService.getByEmail(email2)).willReturn(Optional.of(user2));
    
    given(permissionService.getPermissionTypeBetween(user1, note)).willReturn(Optional.of(p1.getType()));
    given(permissionService.getPermissionTypeBetween(user2, note)).willReturn(Optional.of(p2.getType()));
    
    final String json = new ObjectMapper().writeValueAsString(new EditPermissionDTO(list));
    
    mvc.perform(put("/editPermissions/" + noteId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isNotFound())
            .andExpect(status().reason("Permission Type Not Found"));
  }
  
  @Test
  public void editPermissions_WhenTryingToChangeOwnerPermission_ShouldReturn400() throws Exception {
    final long noteId = 1;
    final Note note = new Note();
    final User owner = new User();
    
    final String email1 = "a@a.com";
    final String email2 = "b@b.com";
    
    final User user1 = new User("user1", "one", email1, "asd");
    final User user2 = new User("user2", "two", email2, "asd");
    
    final Permission p1 = new Permission(user1, note, PermissionType.Owner);
    final Permission p2 = new Permission(user2, note, PermissionType.Viewer);
    
    final List<PermissionDTO> list = new ArrayList<>();
    list.add(new PermissionDTO(email1, "Viewer"));
    list.add(new PermissionDTO(email2, "Editor"));
    
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getOwner(note)).willReturn(Optional.of(owner));
    final String ownerEmail = mockUserAuthentication();
    given(userService.getByEmail(ownerEmail)).willReturn(Optional.of(owner));
    
    given(userService.getByEmail(email1)).willReturn(Optional.of(user1));
    given(userService.getByEmail(email2)).willReturn(Optional.of(user2));
    
    given(permissionService.getPermissionTypeBetween(user1, note)).willReturn(Optional.of(p1.getType()));
    given(permissionService.getPermissionTypeBetween(user2, note)).willReturn(Optional.of(p2.getType()));
    
    final String json = new ObjectMapper().writeValueAsString(new EditPermissionDTO(list));
    
    mvc.perform(put("/editPermissions/" + noteId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason("User has Owner permission that can't be changed"));
  }
  
  @Test
  public void changePin_WhenEverythingIsOk_ShouldReturn200() throws Exception {
    final long noteId = 1;
    final User user = new User();
    final Note note = new Note();
  
    final String email = mockUserAuthentication();
    given(userService.getByEmail(email)).willReturn(Optional.of(user));
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getUsersWithPermissions(note)).willReturn(Collections.singletonList(user));
  
    mvc.perform(put("/changePin/" + noteId))
            .andExpect(status().isOk());
    Mockito.verify(permissionService, times(1)).changePin(user, note);
  }
  
  @Test
  public void changePin_WhenUserHasNoPermissionWithNote_ShouldReturn401() throws Exception {
    final long noteId = 1;
    final User user = new User();
    final Note note = new Note();
    
    final String email = mockUserAuthentication();
    given(userService.getByEmail(email)).willReturn(Optional.of(user));
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    
    mvc.perform(put("/changePin/" + noteId))
            .andExpect(status().isUnauthorized())
            .andExpect(status().reason("User has no Permissions with Note"));
  }
  @Test
  public void changePin_WhenNoteIdIsInvalid_ShouldReturn404() throws Exception {
    final long noteId = 1;
    final User user = new User();
    
    final String email = mockUserAuthentication();
    given(userService.getByEmail(email)).willReturn(Optional.of(user));
    
    mvc.perform(put("/changePin/" + noteId))
            .andExpect(status().isNotFound())
            .andExpect(status().reason("Note Not Found"));
  }
  
  private String mockUserAuthentication() {
    final String email = "user";
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn(email);
    SecurityContextHolder.setContext(securityContext);
    return email;
  }
}
