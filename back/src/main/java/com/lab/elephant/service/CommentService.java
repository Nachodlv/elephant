package com.lab.elephant.service;

import com.lab.elephant.model.Comment;
import com.lab.elephant.model.Note;
import com.lab.elephant.model.User;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {

  Comment addComment(Note note, User user, Comment comment);

  Comment getComment(long id);
}
