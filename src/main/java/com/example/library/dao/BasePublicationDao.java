package com.example.library.dao;

import com.example.library.model.Publication;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePublicationDao<T extends Publication> extends BaseDao<T> implements PublicationDao<T> {

  @Override
  public void createTable() {
    String sql = "CREATE TABLE IF NOT EXISTS publications (" +
        "id IDENTITY PRIMARY KEY, " +
        "type VARCHAR(50), " +
        "title VARCHAR(255), " +
        "publisher VARCHAR(255), " +
        "publish_date DATE, " +
        "reserved BOOLEAN" +
        ");";
    try (Connection conn = DatabaseManager.getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
      createTypeSpecificTable();
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при создании таблицы публикаций", e);
    }
  }

  protected Long generateId() {
    try (Connection conn = conn();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COALESCE(MAX(id), 0) + 1 FROM publications")) {
      if (rs.next()) {
        return rs.getLong(1);
      }
      return 1L;
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при генерации ID", e);
    }
  }

  protected abstract void createTypeSpecificTable();

  protected void insertBasePublication(Publication publication, String type) throws SQLException {
    if (publication.getId() == null) {
      publication.setId(generateId());
    }

    String sql = "INSERT INTO publications (id, title, publisher, publish_date, reserved, type) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = conn();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, publication.getId());
      ps.setString(2, publication.getTitle());
      ps.setString(3, publication.getPublisher());
      if (publication.getPublishDate() != null) {
        ps.setDate(4, Date.valueOf(publication.getPublishDate()));
      } else {
        ps.setNull(4, Types.DATE);
      }
      ps.setBoolean(5, publication.isReserved());
      ps.setString(6, type);
      ps.executeUpdate();
    }
  }

  protected void updateBasePublication(Publication publication) throws SQLException {
    String sql = "UPDATE publications SET title=?, publisher=?, publish_date=?, reserved=? WHERE id=?";
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, publication.getTitle());
      ps.setString(2, publication.getPublisher());
      ps.setDate(3, publication.getPublishDate() != null ? Date.valueOf(publication.getPublishDate()) : null);
      ps.setBoolean(4, publication.isReserved());
      ps.setLong(5, publication.getId());

      ps.executeUpdate();
    }
  }

  @Override
  public List<T> findByType(String type) {
    List<T> result = new ArrayList<>();
    String sql = "SELECT * FROM publications WHERE type = ?";
    try (Connection conn = conn();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, type);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          Long id = rs.getLong("id");
          T publication = findTypeSpecificById(id);
          if (publication != null) {
            result.add(publication);
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при поиске публикаций по типу", e);
    }
    return result;
  }

  @Override
  public List<T> findByTitle(String title) {
    List<T> result = new ArrayList<>();
    String sql = "SELECT * FROM publications WHERE title LIKE ?";
    try (Connection conn = conn();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, "%" + title + "%");
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          Long id = rs.getLong("id");
          T publication = findTypeSpecificById(id);
          if (publication != null) {
            result.add(publication);
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при поиске публикаций по названию", e);
    }
    return result;
  }

  @Override
  public List<T> findByPublisher(String publisher) {
    List<T> result = new ArrayList<>();
    String sql = "SELECT * FROM publications WHERE publisher LIKE ?";
    try (Connection conn = conn();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, "%" + publisher + "%");
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          Long id = rs.getLong("id");
          T publication = findTypeSpecificById(id);
          if (publication != null) {
            result.add(publication);
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при поиске публикаций по издателю", e);
    }
    return result;
  }

  @Override
  protected String deleteSql() {
    return "DELETE FROM publications WHERE id = ?";
  }
  

  protected abstract T findTypeSpecificById(Long id);

  // Метод для преобразования строки даты в LocalDate
  protected LocalDate parseLocalDate(String date) {
    return date != null && !date.isEmpty() ? LocalDate.parse(date) : null;
  }
}
