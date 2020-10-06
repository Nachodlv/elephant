package com.lab.elephant.service;

import com.lab.elephant.model.Comment;
import com.lab.elephant.model.CommentDTO;
import com.lab.elephant.model.Note;
import com.lab.elephant.model.User;
import com.lab.elephant.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;

  public CommentServiceImpl(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  @Override
  public CommentDTO addComment(Note note, User user, Comment comment) {
    comment.setNote(note);
    comment.setOwner(user);
    comment.setCreated(new Timestamp(System.currentTimeMillis()));
    return convertCommentToCommentDTO(commentRepository.save(comment));
  }

  @Override
  public Optional<Comment> getComment(long id) {
    return commentRepository.findById(id);
  }

  @Override
  public List<CommentDTO> getAllCommentsByNote(Note note) {
    List<Comment> comments = commentRepository.findAllByNoteOrderByCreatedDesc(note);
    return convertListCommentToCommentDTO(comments);
  }

  public List<CommentDTO> convertListCommentToCommentDTO(List<Comment> comments) {
    List<CommentDTO> commentDTOS = new ArrayList<>();
    for (Comment comment : comments)
      commentDTOS.add(convertCommentToCommentDTO(comment));
    return commentDTOS;
  }

  public CommentDTO convertCommentToCommentDTO(Comment comment) {
    return new CommentDTO(comment.getUuid(), comment.getContent(), comment.getCreated(),
            comment.getOwner().getFirstName() + " " + comment.getOwner().getLastName());
  }

}
