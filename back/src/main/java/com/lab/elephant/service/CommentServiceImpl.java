package com.lab.elephant.service;

import com.lab.elephant.model.Comment;
import com.lab.elephant.model.Note;
import com.lab.elephant.model.User;
import com.lab.elephant.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
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
    comment.setCreated(new Timestamp(System.currentTimeMillis()));
    return commentRepository.save(comment);
  }

  @Override
  public Optional<Comment> getComment(long id) {
    return commentRepository.findById(id);
  }

  @Override
  public List<Comment> getAllCommentsByNote(Note note) {
    return commentRepository.findAllByNoteOrderByCreatedDesc(note);
  }

}
