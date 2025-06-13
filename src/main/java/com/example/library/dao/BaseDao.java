package com.example.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class BaseDao<T> implements Dao<T> {
  protected Connection conn() throws SQLException {
    return DatabaseManager.getConnection();
  }

  @Override
  public void delete(Long id) throws SQLException {
    String sql = deleteSql();
    try (Connection conn = conn();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, id);
      ps.executeUpdate();
    }
  }

  protected abstract String deleteSql();
}