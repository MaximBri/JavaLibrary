package com.example.library.dao;

import java.util.List;

public interface PublicationDao<T> extends Dao<T> {
  List<T> findByType(String type);

  List<T> findByTitle(String title);

  List<T> findByPublisher(String publisher);
}
