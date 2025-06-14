package com.example.library.service;

import com.example.library.dao.UserDao;
import com.example.library.model.user.User;

public class AuthService {
  private static final String DEFAULT_LIBRARIAN_LOGIN = "librarian";
  private static final String DEFAULT_LIBRARIAN_PASS = "libpass";
  private final UserDao userDao = new UserDao();

  public User login(String login, String password) {
    // 1) Специальная логика для библиотекаря
    if (DEFAULT_LIBRARIAN_LOGIN.equals(login) && DEFAULT_LIBRARIAN_PASS.equals(password)) {
      User existing = userDao.findByLogin(login);
      if (existing == null) {
        return userDao.save(login, login, "LIBRARIAN");
      }
      if (!"LIBRARIAN".equals(existing.getRole())) {
        userDao.updateRole(login, "LIBRARIAN");
        existing = userDao.findByLogin(login);
      }
      return existing;
    }

    // 2) Обычный читатель
    User user = userDao.findByLogin(login);
    if (user != null)
      return user;
    return userDao.save(login, login, "READER");
  }
}