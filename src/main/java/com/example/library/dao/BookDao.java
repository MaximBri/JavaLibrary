package com.example.library.dao;

import com.example.library.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDao {
  public void createTable() {
    String sql = "CREATE TABLE IF NOT EXISTS books (" +
        "id IDENTITY PRIMARY KEY, " +
        "title VARCHAR(255), " +
        "author VARCHAR(255), " +
        "isbn VARCHAR(100) UNIQUE, " +
        "reserved BOOLEAN" +
        ");";
    try (Connection conn = DatabaseManager.getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void add(Book book) {
    String sql = "INSERT INTO books (title, author, isbn, reserved) VALUES (?, ?, ?, ?)";
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, book.getTitle());
      ps.setString(2, book.getAuthor());
      ps.setString(3, book.getIsbn());
      ps.setBoolean(4, book.isReserved());
      ps.executeUpdate();
      ResultSet rs = ps.getGeneratedKeys();
      if (rs.next()) {
        book.setId(rs.getLong(1));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<Book> findAll() {
    List<Book> list = new ArrayList<>();
    String sql = "SELECT * FROM books";
    try (Connection conn = DatabaseManager.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        Book b = new Book();
        b.setId(rs.getLong("id"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setIsbn(rs.getString("isbn"));
        b.setReserved(rs.getBoolean("reserved"));
        list.add(b);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  public void update(Book book) {
    String sql = "UPDATE books SET title=?, author=?, isbn=?, reserved=? WHERE id=?";
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, book.getTitle());
      ps.setString(2, book.getAuthor());
      ps.setString(3, book.getIsbn());
      ps.setBoolean(4, book.isReserved());
      ps.setLong(5, book.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}