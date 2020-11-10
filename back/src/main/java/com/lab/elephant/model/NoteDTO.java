package com.lab.elephant.model;

import java.sql.Timestamp;
import java.util.List;

public class NoteDTO {
  
  private long uuid;
  private String title, content;
  private Timestamp created, LastLocked;
  private List<String> tags;
  private boolean isLocked, isPinned;
  
  
  public NoteDTO() {

  }
  
  public NoteDTO(Note note, boolean isPinned) {
    uuid = note.getUuid();
    title = note.getTitle();
    content = note.getTitle();
    created = note.getCreated();
    LastLocked = note.getLastLocked();
    tags = note.getTags();
    isLocked = note.isLocked();
    this.isPinned = isPinned;
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
  
  public Timestamp getLastLocked() {
    return LastLocked;
  }
  
  public void setLastLocked(Timestamp lastLocked) {
    LastLocked = lastLocked;
  }
  
  public List<String> getTags() {
    return tags;
  }
  
  public void setTags(List<String> tags) {
    this.tags = tags;
  }
  
  public boolean isLocked() {
    return isLocked;
  }
  
  public void setLocked(boolean locked) {
    isLocked = locked;
  }
  
  public boolean isPinned() {
    return isPinned;
  }
  
  public void setPinned(boolean pinned) {
    isPinned = pinned;
  }
}
