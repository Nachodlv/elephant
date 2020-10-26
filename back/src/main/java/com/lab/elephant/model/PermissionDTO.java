package com.lab.elephant.model;

public class PermissionDTO {
  
  private String email;
  private String type;
  
  
  public PermissionDTO(Permission permission) {
    email = permission.getUser().getEmail();
    type = permission.getType().toString();
  }
  
  public PermissionDTO(String email, String type) {
    this.email = email;
    this.type = type;
  }
  
  public PermissionDTO() {
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public String getType() {
    return type;
  }
  
  public void setType(String type) {
    this.type = type;
  }
}
