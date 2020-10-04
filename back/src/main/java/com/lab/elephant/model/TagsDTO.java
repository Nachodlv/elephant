package com.lab.elephant.model;

import javax.validation.constraints.NotNull;
import java.util.List;

public class TagsDTO {
  @NotNull
  private List<String> tags;
  
  public TagsDTO(@NotNull List<String> tags) {
    this.tags = tags;
  }
  
  public TagsDTO() {
  }
  
  public List<String> getTags() {
    return tags;
  }
  
  public void setTags(List<String> tags) {
    this.tags = tags;
  }
}
