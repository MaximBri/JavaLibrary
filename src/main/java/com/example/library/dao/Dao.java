package com.example.library.dao;

import java.sql.SQLException;
import java.util.List;

public interface Dao<T> {
  void createTable();

  void add(T entity);

  void update(T entity);

  void delete(Long id) throws SQLException;

  List<T> findAll();
}