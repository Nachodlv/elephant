package com.lab.elephant.model;

public class ShareNoteDTO {
  private String email;
  private String permissionType;
  
  public ShareNoteDTO(String email, String permissionType) {
    this.email = email;
    this.permissionType = permissionType;
  }
  
  public ShareNoteDTO() {
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public String getPermissionType() {
    return permissionType;
  }
  
  public void setPermissionType(String permissionType) {
    this.permissionType = permissionType;
  }
}
