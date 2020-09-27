package com.lab.elephant.model;

import javax.validation.constraints.NotNull;

public class EditUserDTO {
  @NotNull
  private String firstName;
  @NotNull
  private String lastName;
  
  public EditUserDTO() {
  }
  
  public EditUserDTO(@NotNull String firstName, @NotNull String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }
  
  public String getFirstName() {
    return firstName;
  }
  
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}
