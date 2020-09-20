package com.lab.elephant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
  @Id
  @GeneratedValue
  private long uuid;

  @NotNull
  private String lastName;
  @NotNull
  private String firstName;
  @NotNull
  private String email;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @NotNull
  private String password;
  @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
  private List<Permission> permissions = new ArrayList<>();
  

  @JsonIgnore
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
  private List<Comment> comments = new ArrayList<>();

  public User() {
  }

  public User(String firstName, String lastName, String email, String password) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
  }

  public long getUuid() {
    return uuid;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUuid(long uuid) {
    this.uuid = uuid;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  public void addComment(Comment comment) {
    comments.add(comment);
  }
  
  public List<Permission> getPermissions() {
    return permissions;
  }
  
  public void setPermissions(List<Permission> permissions) {
    this.permissions = permissions;
  }
}
