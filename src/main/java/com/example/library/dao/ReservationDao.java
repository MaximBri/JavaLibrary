package com.example.library.dao;

import com.example.library.model.Reservation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDao {
  public void createTable() {
    String sql = "CREATE TABLE IF NOT EXISTS reservations (" +
        "id IDENTITY PRIMARY KEY, " +
        "book_id BIGINT, " +
        "customer_name VARCHAR(255), " +
        "due_date DATE, " +
        "FOREIGN KEY (book_id) REFERENCES books(id)" +
        ");";
    try (Connection conn = DatabaseManager.getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void add(Reservation reservation) {
    String sql = "INSERT INTO reservations (book_id, customer_name, due_date) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setLong(1, reservation.getBookId());
      ps.setString(2, reservation.getCustomerName());
      ps.setDate(3, Date.valueOf(reservation.getDueDate()));
      ps.executeUpdate();
      ResultSet rs = ps.getGeneratedKeys();
      if (rs.next())
        reservation.setId(rs.getLong(1));
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<Reservation> findByBookId(Long bookId) {
    List<Reservation> list = new ArrayList<>();
    String sql;
    if (bookId != null) {
      sql = "SELECT * FROM reservations WHERE book_id = ?";
    } else {
      sql = "SELECT * FROM reservations";
    }
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      if (bookId != null) {
        ps.setLong(1, bookId);
      }
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
      e.printStackTrace();
    }
    return list;
  }

  public void delete(Long reservationId) {
    String sql = "DELETE FROM reservations WHERE id = ?";
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, reservationId);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}