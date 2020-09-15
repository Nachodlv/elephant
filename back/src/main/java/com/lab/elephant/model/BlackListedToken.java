package com.lab.elephant.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class BlackListedToken {
  @Id
  @GeneratedValue
  private long uuid;
  
  private String token;
  
  public BlackListedToken() {
  }
  
  public BlackListedToken(long uuid, String token) {
    this.uuid = uuid;
    this.token = token;
  }
  
  public BlackListedToken(String token) {
    this.token = token;
  }
  
  public long getUuid() {
    return uuid;
  }
  
  public void setUuid(long uuid) {
    this.uuid = uuid;
  }
  
  public String getToken() {
    return token;
  }
  
  public void setToken(String token) {
    this.token = token;
  }
}
