package com.lab.elephant.controller;

import com.lab.elephant.model.Note;
import com.lab.elephant.model.Permission;
import com.lab.elephant.model.PermissionType;
import com.lab.elephant.model.User;
import com.lab.elephant.security.UserDetailsServiceImpl;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


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
  
  @Test
  public void addPermission_WithEverythingOk_ShouldReturn_200() throws Exception {
    //todo finish implementing
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
    friend.setEmail(email);
    PermissionType permissionType = PermissionType.Viewer;
    //this is to mock the logged in user
    Authentication a = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(a);
    Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn("owner");
    SecurityContextHolder.setContext(securityContext);
    given(userService.getByEmail("owner")).willReturn(Optional.of(owner));
    //le tengo que pasar la info de
    //userEmail, noteId, permissionType
    mvc.perform(post("/permission/add"));
  }
}
