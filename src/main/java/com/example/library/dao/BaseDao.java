package com.example.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class BaseDao<T> implements Dao<T> {
  protected Connection conn() throws SQLException {
    return DatabaseManager.getConnection();
  }

  @Override
  public void delete(Long id) {
    String sql = deleteSql();
    try (PreparedStatement ps = conn().prepareStatement(sql)) {
      ps.setLong(1, id);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  protected abstract String deleteSql();
}