package com.lab.elephant.model;

import javax.validation.constraints.NotNull;

public class UpdatePasswordDto {
  @NotNull
  private String oldPassword;
  @NotNull
  private String newPassword;
  
  public UpdatePasswordDto(String oldPassword, String newPassword) {
    this.oldPassword = oldPassword;
    this.newPassword = newPassword;
  }
  
  public UpdatePasswordDto() {
  }
  
  public String getOldPassword() {
    return oldPassword;
  }
  
  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }
  
  public String getNewPassword() {
    return newPassword;
  }
  
  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}
