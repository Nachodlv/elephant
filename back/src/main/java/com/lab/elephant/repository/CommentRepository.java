package com.lab.elephant.repository;

import com.lab.elephant.model.Comment;
import com.lab.elephant.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  List<Comment> findAllByNoteOrderByCreatedDesc(Note note);

}
