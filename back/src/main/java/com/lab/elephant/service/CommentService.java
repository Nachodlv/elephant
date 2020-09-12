package com.lab.elephant.service;

import com.lab.elephant.model.Comment;
import com.lab.elephant.model.Note;
import com.lab.elephant.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CommentService {

  Comment addComment(Note note, User user, Comment comment);

  Optional<Comment> getComment(long id);
}
