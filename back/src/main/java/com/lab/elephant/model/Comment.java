package com.lab.elephant.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "comment")
public class Comment {

  @Id
  @GeneratedValue
  private long uuid;

  private String content;

  @ManyToOne
  @JoinColumn(name = "owner_id")
  private User owner;

  @ManyToOne
  @JoinColumn(name = "note_id")
  private Note note;

  private Timestamp created;

  public Comment(String content, User owner, Note note, Timestamp created) {
    this.content = content;
    this.owner = owner;
    this.note = note;
    this.created = created;
  }

  public Comment(String content) {
    this.content = content;
  }

  public Comment() {
  }

  public long getUuid() {
    return uuid;
  }

  public void setUuid(long uuid) {
    this.uuid = uuid;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    owner.addComment(this);
    this.owner = owner;
  }

  public Note getNote() {
    return note;
  }

  public void setNote(Note note) {
    note.addComment(this);
    this.note = note;
  }

  public Timestamp getCreated() {
    return created;
  }

  public void setCreated(Timestamp created) {
    this.created = created;
  }
}
