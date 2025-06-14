package com.example.library.model.user;

public class Librarian extends User {
  public Librarian(Long id, String login, String name) {
    super(id, login, name, "LIBRARIAN");
  }

  @Override
  public boolean canAddPublication() {
    return true;
  }

  @Override
  public boolean canDeletePublication() {
    return true;
  }

  @Override
  public boolean canEditPublication() {
    return true;
  }

  @Override
  public boolean canReservePublication() {
    return true;
  }
}