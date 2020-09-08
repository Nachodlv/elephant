package com.lab.elephant.service;

import com.lab.elephant.model.Comment;
import com.lab.elephant.model.Note;
import com.lab.elephant.model.User;
import com.lab.elephant.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;

  public CommentServiceImpl(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  @Override
  public Comment addComment(Note note, User user, Comment comment) {
    comment.setNote(note);
    comment.setOwner(user);
    return commentRepository.save(comment);
  }

  @Override
  public Comment getComment(long id) {
    Optional<Comment> optionalComment = commentRepository.findById(id);
    return optionalComment.get();
  }


}
