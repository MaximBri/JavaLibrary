package com.example.library.model.user;

public class Reader extends User {
  public Reader(Long id, String login, String name) {
    super(id, login, name, "READER");
  }

  @Override
  public boolean canAddPublication() {
    return false;
  }

  @Override
  public boolean canDeletePublication() {
    return false;
  }

  @Override
  public boolean canEditPublication() {
    return false;
  }

  @Override
  public boolean canReservePublication() {
    return true;
  }
}
