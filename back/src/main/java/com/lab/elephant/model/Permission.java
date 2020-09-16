package com.lab.elephant.model;

import javax.persistence.*;

@Entity
public class Permission {
  @Id
  @GeneratedValue
  private long uuid;
  
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;
  @ManyToOne(fetch = FetchType.LAZY)
  private Note note;
  
  private PermissionType type;
  
  public Permission() {
  }
  
  public Permission(long uuid, User user, Note note, PermissionType type) {
    this.uuid = uuid;
    this.user = user;
    this.note = note;
    this.type = type;
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
}
