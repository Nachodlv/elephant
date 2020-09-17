package com.lab.elephant.controller;

import com.lab.elephant.model.Comment;
import com.lab.elephant.model.CommentDTO;
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
import java.util.List;
import java.util.Optional;

import static com.lab.elephant.security.SecurityConstants.HEADER_STRING;

@CrossOrigin
@RestController
@RequestMapping(path = "/comment")
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
  public CommentDTO addComment(@PathVariable("idNote") long idNote, @RequestBody @Valid Comment comment, HttpServletRequest request) {

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
          return commentService.addComment(note, user, comment);
        } else {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note Not Found");
        }
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
      }
    }
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token Not Found");
  }

  @GetMapping(path = "/all/{idNote}")
  public List<CommentDTO> getCommentsByNoteInOrderByDateCreated(@PathVariable("idNote") long idNote) {
    Optional<Note> optionalNote = noteService.getNote(idNote);
    if (optionalNote.isPresent()) {
      Note note = optionalNote.get();
      return commentService.getAllCommentsByNote(note);
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note Not Found");
  }
}
