package com.lab.elephant.model;

import java.util.List;

public class EditPermissionDTO {
  private List<PermissionDTO> list;
  
  public EditPermissionDTO(List<PermissionDTO> list) {
    this.list = list;
  }
  
  public EditPermissionDTO() {
  }
  
  public List<PermissionDTO> getList() {
    return list;
  }
  
  public void setList(List<PermissionDTO> list) {
    this.list = list;
  }
}
