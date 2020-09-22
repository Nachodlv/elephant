package com.lab.elephant.model;

import java.sql.Timestamp;

public class CommentDTO {

  private long uuid;
  private String content;
  private Timestamp created;
  private String ownerName;

  public CommentDTO(long uuid, String content, Timestamp created, String ownerName) {
    this.uuid = uuid;
    this.content = content;
    this.created = created;
    this.ownerName = ownerName;
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

  public Timestamp getCreated() {
    return created;
  }

  public void setCreated(Timestamp created) {
    this.created = created;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String name) {
    this.ownerName = name;
  }
}
