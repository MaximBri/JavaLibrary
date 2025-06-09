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
        "book_id BIGINT, " +
        "customer_name VARCHAR(255), " +
        "due_date DATE, " +
        "FOREIGN KEY (book_id) REFERENCES books(id)" +
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
    String sql = "INSERT INTO reservations (book_id, customer_name, due_date) VALUES (?, ?, ?)";
    try (Connection conn = conn();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setLong(1, r.getBookId());
      ps.setString(2, r.getCustomerName());
      ps.setDate(3, Date.valueOf(r.getDueDate()));
      ps.executeUpdate();
      ResultSet rs = ps.getGeneratedKeys();
      if (rs.next())
        r.setId(rs.getLong(1));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<Reservation> findAll() {
    return findByBookId(null);
  }

  public List<Reservation> findByBookId(Long bookId) {
    List<Reservation> list = new ArrayList<>();
    String sql = bookId == null
        ? "SELECT * FROM reservations"
        : "SELECT * FROM reservations WHERE book_id=?";
    try (Connection conn = conn();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      if (bookId != null)
        ps.setLong(1, bookId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        Reservation r = new Reservation();
        r.setId(rs.getLong("id"));
        r.setBookId(rs.getLong("book_id"));
        r.setCustomerName(rs.getString("customer_name"));
        r.setDueDate(rs.getDate("due_date").toLocalDate());
        list.add(r);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return list;
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
  public void delete(Long id) {
    super.delete(id);
  }
}
