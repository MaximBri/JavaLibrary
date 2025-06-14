package com.example.library.dao;

import com.example.library.model.user.Librarian;
import com.example.library.model.user.Reader;
import com.example.library.model.user.User;

import java.sql.*;

public class UserDao {
  private static final String JDBC_URL = "jdbc:h2:~/librarydb";
  private static final String USER = "sa";
  private static final String PASS = "";

  public UserDao() {
    // Инициализация таблицы
    try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
      stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
          "id IDENTITY PRIMARY KEY, " +
          "login VARCHAR(50) UNIQUE NOT NULL, " +
          "name VARCHAR(100), " +
          "role VARCHAR(20) NOT NULL)");
    } catch (SQLException e) {
      throw new RuntimeException("Error initializing User table", e);
    }
  }

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(JDBC_URL, USER, PASS);
  }

  public User findByLogin(String login) {
    String sql = "SELECT id, login, name, role FROM users WHERE login = ?";
    try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, login);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String role = rs.getString("role");
        if ("LIBRARIAN".equals(role)) {
          return new Librarian(id, login, name);
        } else {
          return new Reader(id, login, name);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error querying user", e);
    }
    return null;
  }

  public void updateRole(String login, String newRole) {
    String sql = "UPDATE users SET role = ? WHERE login = ?";
    try (Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, newRole);
      ps.setString(2, login);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error updating user role", e);
    }
  }

  public User save(String login, String name, String role) {
    String sql = "INSERT INTO users(login, name, role) VALUES (?, ?, ?)";
    try (Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, login);
      ps.setString(2, name);
      ps.setString(3, role);
      ps.executeUpdate();
      ResultSet keys = ps.getGeneratedKeys();
      if (keys.next()) {
        Long id = keys.getLong(1);
        if ("LIBRARIAN".equals(role)) {
          return new Librarian(id, login, name);
        } else {
          return new Reader(id, login, name);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error saving user", e);
    }
    return null;
  }
}
