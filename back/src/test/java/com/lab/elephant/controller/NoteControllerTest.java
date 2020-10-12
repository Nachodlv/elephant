package com.lab.elephant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.elephant.model.Note;
import com.lab.elephant.model.TagsDTO;
import com.lab.elephant.model.User;
import com.lab.elephant.security.UserDetailsServiceImpl;
import com.lab.elephant.service.BlackListedTokenServiceImpl;
import com.lab.elephant.service.NoteServiceImpl;
import com.lab.elephant.service.PermissionService;
import com.lab.elephant.service.UserService;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(NoteController.class)
@AutoConfigureMockMvc(addFilters = false)
public class NoteControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private NoteServiceImpl noteService;
  @MockBean
  private UserService userService;
  // All of the MockBeans below
  // are not used but are necessary for the tests to run.
  @MockBean
  private UserDetailsServiceImpl userDetailsService;
  @MockBean
  private BCryptPasswordEncoder passwordEncoder;
  @MockBean
  private BlackListedTokenServiceImpl tokenService;
  @MockBean
  private PermissionService permissionService;
  
  @Test
  public void addNewNote_WhenNoteCreated_ShouldReturnNewNote() throws Exception {
    Timestamp ts = new Timestamp(new Date().getTime());
    Note note = new Note("Nueva Nota", "", ts);
    Optional<Note> optionalNote = Optional.of(note);
    final String noteJson = objectMapper.writeValueAsString(note);
    
    given(noteService.getNote(note.getUuid())).willReturn(optionalNote);
  
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(new User()));
    
    mvc.perform(post("/note/new").content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
  }

  @Test
  public void addNewNote_WhenTitleLengthIsMoreThan60_ShouldReturnBadRequest() throws Exception {
    Timestamp ts = new Timestamp(new Date().getTime());
    Note note = new Note("Este titulo contiene mas de 60 letras, por lo cual debe tirar un error",
            "", ts);

    final String noteJson = objectMapper.writeValueAsString(note);

    mvc.perform(post("/note/new").content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());
  }

  @Test
  public void getANote_WhenNoteAddedAndUserCanView_ShouldReturnTheNote() throws Exception {
    Note note1 = new Note("Nueva nota 1");
    User user = new User();
    noteService.addNote(note1, user);
    List<User> userList = new ArrayList<>();
    userList.add(user);
    
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(user));
    given(noteService.getUsersWithPermissions(note1)).willReturn(userList);
    given(noteService.getNote(1L)).willReturn(Optional.of(note1));
    final String noteJson = objectMapper.writeValueAsString(note1);

    mvc.perform(get("/note/1").content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
  }

  @Test
  public void getANote_WhenUserCantView_ShouldReturn401() throws Exception {
    Note note1 = new Note("Nueva nota 1");
    User user = new User();
    noteService.addNote(note1, user);
  
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(user));
    given(noteService.getNote(1L)).willReturn(Optional.of(note1));
    final String noteJson = objectMapper.writeValueAsString(note1);
  
    mvc.perform(get("/note/1").content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isUnauthorized())
            .andExpect(status().reason("User cannot view this note"));
  }
  
  @Test
  public void getNote_WhenIdDoesNotExists_ShouldReturnNotFound() throws Exception {
    given(noteService.getNote(1L)).willReturn(Optional.empty());
  
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(new User()));
    
    mvc.perform(get("/note/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

  @Test
  public void deleteNote_WhenNoteExistsAndUserIsOwner_ShouldDeleteIt() throws Exception {
    Note note = new Note("This is the new note");
    User user = new User();
    user.setUuid(1);
    noteService.addNote(note, user);
  
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(user));
    
    given(noteService.getOwner(note)).willReturn(Optional.of(user));
    given(noteService.getNote(1L)).willReturn(Optional.of(note));
    final String noteJson = objectMapper.writeValueAsString(note);

    mvc.perform(delete("/note/delete/1").content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
  }
  
  @Test
  public void deleteNote_WhenNoteHasNoOwner_ShouldReturn500() throws Exception {
    Note note = new Note("This is the new note");
    noteService.addNote(note, new User());
    
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(new User()));
    
    given(noteService.getNote(1L)).willReturn(Optional.of(note));
    final String noteJson = objectMapper.writeValueAsString(note);
    
    mvc.perform(delete("/note/delete/1").content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isInternalServerError())
            .andExpect(status().reason("Note has no owner"));
  }
  
  @Test
  public void deleteNote_WhenNoteExistsAndUserIsNotOwner_ShouldReturn401() throws Exception {
    Note note = new Note("This is the new note");
    User user = new User();
    User owner = new User();
    user.setUuid(1);
    owner.setUuid(2);
    noteService.addNote(note, user);
  
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("owner");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("owner")).willReturn(Optional.of(owner));
  
    given(noteService.getOwner(note)).willReturn(Optional.of(user));
    given(noteService.getNote(1L)).willReturn(Optional.of(note));
    final String noteJson = objectMapper.writeValueAsString(note);
  
    mvc.perform(delete("/note/delete/1").content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isUnauthorized())
            .andExpect(status().reason("User can't delete this note"));
  }
  
  @Test
  public void deleteNote_WhenNoteNotExists_ShouldReturnNotFound() throws Exception {
    given(noteService.getNote(1L)).willReturn(Optional.empty());
  
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(new User()));
    
    mvc.perform(delete("/note/delete/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound());
  }
  
  @Test
  public void addTags_WhenEverythingIsOk_ShouldReturn200() throws Exception {
    final Note note = new Note();
    final long id = 1;
    final User user = new User();
    final List<User> users = new ArrayList<>();
    users.add(user);
    //this is mocking the user Authentication
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(user));
    
    given(noteService.getNote(id)).willReturn(Optional.of(note));
    given(noteService.getUsersWithEditOrOwner(note)).willReturn(users);
    List<String> tags = new ArrayList<>();
    tags.add("fun");
    tags.add("food");
    tags.add("frozen");
    
    ObjectMapper o = new ObjectMapper();
    String json = o.writeValueAsString(new TagsDTO(tags));
    mvc.perform(put("/note/addTags/" + id).content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }
  
  @Test
  public void addTags_WhenNoteDoesNotExist_ShouldReturn404() throws Exception {
    final Note note = new Note();
    final long id = 1;
    final User user = new User();
    final List<User> users = new ArrayList<>();
    users.add(user);
    //this is mocking the user Authentication
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(user));
    
    given(noteService.getNote(id)).willReturn(Optional.empty());
    given(noteService.getUsersWithEditOrOwner(note)).willReturn(users);
    List<String> tags = new ArrayList<>();
    tags.add("fun");
    tags.add("food");
    tags.add("frozen");
    
    ObjectMapper o = new ObjectMapper();
    String json = o.writeValueAsString(new TagsDTO(tags));
    mvc.perform(put("/note/addTags/" + id).content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason("Note Not Found"));
  }
  
  @Test
  public void addTags_WhenUserIsUnauthorized_ShouldReturn401() throws Exception {
    final Note note = new Note();
    final long id = 1;
    //this is mocking the user Authentication
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(new User()));
    
    given(noteService.getNote(id)).willReturn(Optional.of(note));
    given(noteService.getUsersWithEditOrOwner(note)).willReturn(new ArrayList<>());
    List<String> tags = new ArrayList<>();
    tags.add("fun");
    tags.add("food");
    tags.add("frozen");
  
    ObjectMapper o = new ObjectMapper();
    String json = o.writeValueAsString(new TagsDTO(tags));
    mvc.perform(put("/note/addTags/" + id).content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(status().reason("User cannot add Tags to this note"));
  }
  
  @Test
  public void startEdit_WhenEverythingIsOkAndNoteIsNotLocked_ShouldReturnOkAndTrue() throws Exception {
    final Note note = new Note();
    final User user = new User();
    final List<User> users = new ArrayList<>();
    final long noteId = 1;
    note.setLocked(false);
    users.add(user);
    //this is mocking the user Authentication
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(user));
    
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getUsersWithEditOrOwner(note)).willReturn(users);
    
    
    
    final MvcResult result = mvc.perform(get("/note/startEdit/" + noteId))
            .andExpect(status().isOk())
            .andReturn();
    final String contentAsString = result.getResponse().getContentAsString();
    assertThat(contentAsString).isEqualTo("true");
  }
  
  @Test
  public void startEdit_WhenEverythingIsOkAndNoteIsLockedButCanBeEdited_ShouldReturnOkAndTrue() throws Exception {
    final Note note = new Note();
    final User user = new User();
    final List<User> users = new ArrayList<>();
    final long noteId = 1;
    note.setLocked(true);
    note.setLastLocked(new Timestamp(System.currentTimeMillis() - 100000000));
    users.add(user);
    //this is mocking the user Authentication
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(user));
    
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getUsersWithEditOrOwner(note)).willReturn(users);
    
    
    
    final MvcResult result = mvc.perform(get("/note/startEdit/" + noteId))
            .andExpect(status().isOk())
            .andReturn();
    final String contentAsString = result.getResponse().getContentAsString();
    assertThat(contentAsString).isEqualTo("true");
  }
  
  @Test
  public void startEdit_WhenEverythingIsOkAndNoteIsLockedButCantBeEdited_ShouldReturnOkAndFalse() throws Exception {
    final Note note = new Note();
    final User user = new User();
    final List<User> users = new ArrayList<>();
    final long noteId = 1;
    note.setLocked(true);
    note.setLastLocked(new Timestamp(System.currentTimeMillis()));
    users.add(user);
    //this is mocking the user Authentication
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(user));
    
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getUsersWithEditOrOwner(note)).willReturn(users);
    
    
    
    final MvcResult result = mvc.perform(get("/note/startEdit/" + noteId))
            .andExpect(status().isOk())
            .andReturn();
    final String contentAsString = result.getResponse().getContentAsString();
    assertThat(contentAsString).isEqualTo("false");
  }
  
  @Test
  public void startEdit_WhenNoteDoesNotExist_ShouldReturn404() throws Exception {
    final long noteId = 1;
    given(noteService.getNote(noteId)).willReturn(Optional.empty());
   
    mvc.perform(get("/note/startEdit/" + noteId))
            .andExpect(status().isNotFound())
            .andExpect(status().reason("Note Not Found"));
  }
  
  @Test
  public void startEdit_WhenUserIsUnAuthorized_ShouldReturn401() throws Exception {
    final Note note = new Note();
    final User user = new User();
    final List<User> users = new ArrayList<>();
    final long noteId = 1;
    //this is mocking the user Authentication
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(user));
    
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getUsersWithEditOrOwner(note)).willReturn(users);
    
    
    
    mvc.perform(get("/note/startEdit/" + noteId))
            .andExpect(status().isUnauthorized())
            .andExpect(status().reason("User cannot edit this note"));
  }
  
  @Test
  public void autoSave_WhenEverythingIsOk_ShouldReturn200() throws Exception {
    final Note note = new Note();
    final User user = new User();
    final List<User> users = new ArrayList<>();
    final long noteId = 1;
    users.add(user);
    note.setTitle("Title");
    //this is mocking the user Authentication
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(user));
  
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getUsersWithEditOrOwner(note)).willReturn(users);
    
    final ObjectMapper o = new ObjectMapper();
    String noteJson = o.writeValueAsString(note);
    mvc.perform(put("/note/autoSave/" + noteId).content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }
  
  @Test
  public void autoSave_WhenNoteDoesNotExist_ShouldReturn404() throws Exception {
    final Note note = new Note();
    final long noteId = 1;
    note.setTitle("Title");
    
    given(noteService.getNote(noteId)).willReturn(Optional.empty());
    
    final ObjectMapper o = new ObjectMapper();
    String noteJson = o.writeValueAsString(note);
    mvc.perform(put("/note/autoSave/" + noteId).content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason("Note Not Found"));
  }
  
  @Test
  public void autoSave_WhenUserIsUnauthorized_ShouldReturn401() throws Exception {
    final Note note = new Note();
    final User user = new User();
    final List<User> users = new ArrayList<>();
    final long noteId = 1;
    note.setTitle("Title");
    //this is mocking the user Authentication
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(user));
    
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getUsersWithEditOrOwner(note)).willReturn(users);
    
    final ObjectMapper o = new ObjectMapper();
    String noteJson = o.writeValueAsString(note);
    mvc.perform(put("/note/autoSave/" + noteId).content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(status().reason("User cannot edit this note"));
  }
  
  @Test
  public void endEdit_WhenEverythingIsOk_ShouldReturn200() throws Exception {
    final Note note = new Note();
    final User user = new User();
    final List<User> users = new ArrayList<>();
    final long noteId = 1;
    users.add(user);
    note.setTitle("Title");
    //this is mocking the user Authentication
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(user));
  
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getUsersWithEditOrOwner(note)).willReturn(users);
  
    final ObjectMapper o = new ObjectMapper();
    String noteJson = o.writeValueAsString(note);
    mvc.perform(put("/note/endEdit/" + noteId).content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }
  
  @Test
  public void endEdit_WhenNoteDoesNotExist_ShouldReturn404() throws Exception {
    final Note note = new Note();
    final long noteId = 1;
    note.setTitle("Title");
    
    given(noteService.getNote(noteId)).willReturn(Optional.empty());
    
    final ObjectMapper o = new ObjectMapper();
    String noteJson = o.writeValueAsString(note);
    mvc.perform(put("/note/endEdit/" + noteId).content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason("Note Not Found"));
  }
  
  @Test
  public void endEdit_WhenUserIsUnauthorized_ShouldReturn401() throws Exception {
    final Note note = new Note();
    final User user = new User();
    final List<User> users = new ArrayList<>();
    final long noteId = 1;
    note.setTitle("Title");
    //this is mocking the user Authentication
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("user")).willReturn(Optional.of(user));
    
    given(noteService.getNote(noteId)).willReturn(Optional.of(note));
    given(noteService.getUsersWithEditOrOwner(note)).willReturn(users);
    
    final ObjectMapper o = new ObjectMapper();
    String noteJson = o.writeValueAsString(note);
    mvc.perform(put("/note/endEdit/" + noteId).content(noteJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(status().reason("User cannot edit this note"));
  }
}
