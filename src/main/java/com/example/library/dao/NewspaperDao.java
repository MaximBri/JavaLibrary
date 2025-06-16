package com.example.library.dao;

import com.example.library.model.Newspaper;
import java.sql.*;
import java.util.List;

public class NewspaperDao extends BasePublicationDao<Newspaper> {

  @Override
  protected void createTypeSpecificTable() {
    String sql = "CREATE TABLE IF NOT EXISTS newspapers (" +
        "id BIGINT PRIMARY KEY, " +
        "editor_name VARCHAR(255), " +
        "frequency VARCHAR(50), " +
        "is_national BOOLEAN, " +
        "language VARCHAR(50), " +
        "FOREIGN KEY (id) REFERENCES publications(id) ON DELETE CASCADE" +
        ");";
    try (Connection conn = DatabaseManager.getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при создании таблицы газет", e);
    }
  }

  @Override
  public void add(Newspaper newspaper) {
    try {
      insertBasePublication(newspaper, "NEWSPAPER");

      String sql = "INSERT INTO newspapers (id, editor_name, frequency, is_national, language) VALUES (?, ?, ?, ?, ?)";
      try (Connection conn = DatabaseManager.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setLong(1, newspaper.getId());
        ps.setString(2, newspaper.getEditorName());
        ps.setString(3, newspaper.getFrequency());
        ps.setBoolean(4, newspaper.isNational());
        ps.setString(5, newspaper.getLanguage());
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при добавлении газеты", e);
    }
  }

  @Override
  public void update(Newspaper newspaper) {
    try {
      updateBasePublication(newspaper);

      String sql = "UPDATE newspapers SET editor_name=?, frequency=?, is_national=?, language=? WHERE id=?";
      try (Connection conn = DatabaseManager.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, newspaper.getEditorName());
        ps.setString(2, newspaper.getFrequency());
        ps.setBoolean(3, newspaper.isNational());
        ps.setString(4, newspaper.getLanguage());
        ps.setLong(5, newspaper.getId());
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при обновлении газеты", e);
    }
  }

  @Override
  public List<Newspaper> findAll() {
    return findByType("NEWSPAPER");
  }

  @Override
  protected Newspaper findTypeSpecificById(Long id) {
    String sql = "SELECT p.*, n.editor_name, n.frequency, n.is_national, n.language " +
        "FROM publications p " +
        "JOIN newspapers n ON p.id = n.id " +
        "WHERE p.id = ?";
    try (Connection conn = conn();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Newspaper newspaper = new Newspaper();
          newspaper.setId(rs.getLong("id"));
          newspaper.setTitle(rs.getString("title"));
          newspaper.setPublisher(rs.getString("publisher"));
          Date publishDate = rs.getDate("publish_date");
          if (publishDate != null) {
            newspaper.setPublishDate(publishDate.toLocalDate());
          }
          newspaper.setReserved(rs.getBoolean("reserved"));
          newspaper.setEditorName(rs.getString("editor_name"));
          newspaper.setFrequency(rs.getString("frequency"));
          newspaper.setNational(rs.getBoolean("is_national"));
          newspaper.setLanguage(rs.getString("language"));
          return newspaper;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при поиске газеты по ID", e);
    }
    return null;
  }

  @Override
  public void delete(Long id) throws SQLException {
    super.delete(id);
  }
}