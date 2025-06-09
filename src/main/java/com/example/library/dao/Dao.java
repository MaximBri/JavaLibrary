package com.example.library.dao;

import java.util.List;

public interface Dao<T> {
  void createTable();

  void add(T entity);

  void update(T entity);

  void delete(Long id);

  List<T> findAll();
}