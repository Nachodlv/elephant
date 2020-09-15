package com.lab.elephant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "note")
public class Note {

  @Id
  @GeneratedValue
  private long uuid;

  private String title;

  private String content;

  private Timestamp created;

  @JsonIgnore
  @OneToMany(cascade = CascadeType.ALL)
  private List<Comment> comments = new ArrayList<>();

  @JsonIgnore
  @OneToMany(mappedBy = "note", cascade = CascadeType.MERGE)
  private List<Permission> permissions = new ArrayList<>();
  
  public Note(String title, String content, Timestamp created) {
    this.title = title;
    this.content = content;
    this.created = created;
  }

  public Note(String title) {
    this.title = title;
  }

  public Note() {
  }

  public long getUuid() {
    return uuid;
  }

  public void setUuid(long uuid) {
    this.uuid = uuid;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Timestamp getCreated() {
    return created;
  }

  public void setCreated(Timestamp created) {
    this.created = created;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  public void addComment(Comment comment) {
    comments.add(comment);
  }
  
  public List<Permission> getPermissions() {
    return permissions;
  }
  
  public void setPermissions(List<Permission> permissions) {
    this.permissions = permissions;
  }
}
