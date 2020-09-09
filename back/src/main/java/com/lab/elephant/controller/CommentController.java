package com.lab.elephant.controller;

import com.lab.elephant.model.Comment;
import com.lab.elephant.model.Note;
import com.lab.elephant.model.User;
import com.lab.elephant.service.CommentService;
import com.lab.elephant.service.NoteService;
import com.lab.elephant.service.TokenService;
import com.lab.elephant.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

import static com.lab.elephant.security.SecurityConstants.HEADER_STRING;

@CrossOrigin
@RestController
@RequestMapping(value = "/comment")
public class CommentController {

  private final NoteService noteService;
  private final CommentService commentService;
  private final TokenService tokenService;
  private final UserService userService;

  public CommentController(NoteService noteService, CommentService commentService, TokenService tokenService, UserService userService) {
    this.noteService = noteService;
    this.commentService = commentService;
    this.tokenService = tokenService;
    this.userService = userService;
  }

  @PostMapping(path = "/add/{idNote}")
  public Comment addComment(@PathVariable("idNote") long idNote, @RequestBody @Valid Comment comment, HttpServletRequest request) {
    System.out.println("Aca deberia llegar");

    String token = request.getHeader(HEADER_STRING);

    if (token != null) {
      String email = tokenService.getEmailByToken(token);

      if (comment.getContent().length() > 300)
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Content is too long");

      // verify and return if user exists with that email
      Optional<User> optionalUser = userService.getByEmail(email);
      if (optionalUser.isPresent()) {
        User user = optionalUser.get();

        Optional<Note> optionalNote = noteService.getNote(idNote);
        if (optionalNote.isPresent()) {
          Note note = optionalNote.get();
          System.out.println("Aca deberia llegar todo bien");
          return commentService.addComment(note, user, comment);
        } else {
          System.out.println("note");
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note Not Found");
        }
      } else {
        System.out.println("user");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
      }
    }
    System.out.println("token");
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token Not Found");
  }

}
