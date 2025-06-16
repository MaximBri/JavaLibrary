package com.example.library.dao;

import com.example.library.model.Reservation;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class ReservationDao extends BaseDao<Reservation> {
  @Override
  public void createTable() {
    String sql = "CREATE TABLE IF NOT EXISTS reservations (" +
        "id IDENTITY PRIMARY KEY, " +
        "publication_id BIGINT NOT NULL, " + 
        "customer_name VARCHAR(255) NOT NULL, " +
        "due_date DATE NOT NULL" + 
        ")";
    try (Connection conn = conn();
        Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void add(Reservation r) {
    String sql = "INSERT INTO reservations(publication_id, customer_name, due_date) VALUES(?, ?, ?)";
    try (Connection conn = conn();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, r.getBookId());
      ps.setString(2, r.getCustomerName());
      ps.setDate(3, Date.valueOf(r.getDueDate()));
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при добавлении резервации", e);
    }
  }

  @Override
  public List<Reservation> findAll() {
    return findByPublicationId(null);
  }

  public List<Reservation> findByPublicationId(Long publicationId) {
    String sql = publicationId == null
        ? "SELECT id, publication_id, customer_name, due_date FROM reservations"
        : "SELECT id, publication_id, customer_name, due_date FROM reservations WHERE publication_id = ?";
    List<Reservation> results = new ArrayList<>();
    try (Connection conn = conn();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      if (publicationId != null)
        ps.setLong(1, publicationId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        Reservation r = new Reservation(
            rs.getLong("id"),
            rs.getLong("publication_id"),
            rs.getString("customer_name"),
            rs.getDate("due_date").toLocalDate());
        results.add(r);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при получении резерваций", e);
    }
    return results;
  }

  @Override
  public void update(Reservation r) {
    String sql = "UPDATE reservations SET book_id = ?, customer_name = ?, due_date = ? WHERE id = ?";
    try (Connection conn = conn();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, r.getBookId());
      ps.setString(2, r.getCustomerName());
      ps.setDate(3, Date.valueOf(r.getDueDate()));
      ps.setLong(4, r.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected String deleteSql() {
    return "DELETE FROM reservations WHERE id=?";
  }

  @Override
  public void delete(Long id) throws SQLException {
    super.delete(id);
  }
}
