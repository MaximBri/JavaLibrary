package com.example.library.dao;

import com.example.library.model.Magazine;
import java.sql.*;
import java.util.List;

public class MagazineDao extends BasePublicationDao<Magazine> {

  @Override
  protected void createTypeSpecificTable() {
    String sql = "CREATE TABLE IF NOT EXISTS magazines (" +
        "id BIGINT PRIMARY KEY, " +
        "issn VARCHAR(20), " +
        "issue_number INT, " +
        "category VARCHAR(100), " +
        "is_monthly BOOLEAN, " +
        "FOREIGN KEY (id) REFERENCES publications(id) ON DELETE CASCADE" +
        ");";
    try (Connection conn = DatabaseManager.getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при создании таблицы журналов", e);
    }
  }

  @Override
  public void add(Magazine magazine) {
    try {
      insertBasePublication(magazine, "MAGAZINE");

      String sql = "INSERT INTO magazines (id, issn, issue_number, category, is_monthly) VALUES (?, ?, ?, ?, ?)";
      try (Connection conn = DatabaseManager.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setLong(1, magazine.getId());
        ps.setString(2, magazine.getIssn());
        ps.setInt(3, magazine.getIssueNumber());
        ps.setString(4, magazine.getCategory());
        ps.setBoolean(5, magazine.isMonthly());

        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при добавлении журнала", e);
    }
  }

  @Override
  public void update(Magazine magazine) {
    try {
      updateBasePublication(magazine);

      String sql = "UPDATE magazines SET issn=?, issue_number=?, category=?, is_monthly=? WHERE id=?";
      try (Connection conn = DatabaseManager.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, magazine.getIssn());
        ps.setInt(2, magazine.getIssueNumber());
        ps.setString(3, magazine.getCategory());
        ps.setBoolean(4, magazine.isMonthly());
        ps.setLong(5, magazine.getId());

        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при обновлении журнала", e);
    }
  }

  @Override
  public List<Magazine> findAll() {
    return findByType("MAGAZINE");
  }

  @Override
  protected Magazine findTypeSpecificById(Long id) {
    String sql = "SELECT p.*, m.issn, m.issue_number, m.category, m.is_monthly " +
        "FROM publications p " +
        "JOIN magazines m ON p.id = m.id " +
        "WHERE p.id = ?";

    try (Connection conn = conn();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, id);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Magazine magazine = new Magazine();
          magazine.setId(rs.getLong("id"));
          magazine.setTitle(rs.getString("title"));
          magazine.setPublisher(rs.getString("publisher"));
          Date publishDate = rs.getDate("publish_date");
          if (publishDate != null) {
            magazine.setPublishDate(publishDate.toLocalDate());
          }
          magazine.setReserved(rs.getBoolean("reserved"));
          magazine.setIssn(rs.getString("issn"));
          magazine.setIssueNumber(rs.getInt("issue_number"));
          magazine.setCategory(rs.getString("category"));
          return magazine;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при поиске журнала по ID", e);
    }
    return null;
  }

  @Override
  public void delete(Long id) throws SQLException {
    super.delete(id);
  }
}