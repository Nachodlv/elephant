package com.lab.elephant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
public class Permission {
  @Id
  @GeneratedValue
  private long uuid;

  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  @ManyToOne(fetch = FetchType.EAGER)
  private User user;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  private Note note;

  private PermissionType type;

  private boolean isPinned = false;
  
  public Permission() {
  }

  public Permission(long uuid, User user, Note note, PermissionType type, boolean isPinned) {
    this.uuid = uuid;
    this.user = user;
    this.note = note;
    this.type = type;
    this.isPinned = isPinned;
  }

  public Permission(User user, Note note, PermissionType type, boolean isPinned) {
    this.user = user;
    this.note = note;
    this.type = type;
    this.isPinned = isPinned;
  }

  public Permission(User user, Note note, PermissionType type) {
    this.user = user;
    this.note = note;
    this.type = type;
  }

  public long getUuid() {
    return uuid;
  }

  public void setUuid(long uuid) {
    this.uuid = uuid;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Note getNote() {
    return note;
  }

  public void setNote(Note note) {
    this.note = note;
  }

  public PermissionType getType() {
    return type;
  }

  public void setType(PermissionType type) {
    this.type = type;
  }
  
  public boolean isPinned() {
    return isPinned;
  }
  
  public void setPinned(boolean pinned) {
    isPinned = pinned;
  }
  
  public void changePin() {
    isPinned = !isPinned;
  }
}
