package com.lab.elephant.model;

public class DeleteUserDTO {
  
  private String password;
  
  public DeleteUserDTO(String password) {
    this.password = password;
  }
  
  public DeleteUserDTO() {
  }
  
  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
}
