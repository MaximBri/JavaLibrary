package com.example.library.model.user;

public abstract class User {
  private Long id;
  private String login;
  private String name;
  private String role;

  public User(Long id, String login, String name, String role) {
    this.id = id;
    this.login = login;
    this.name = name;
    this.role = role;
  }

  public Long getId() {
    return id;
  }

  public String getLogin() {
    return login;
  }

  public String getName() {
    return name;
  }

  public String getRole() {
    return role;
  }

  public abstract boolean canAddPublication();

  public abstract boolean canDeletePublication();

  public abstract boolean canEditPublication();

  public abstract boolean canReservePublication();
}