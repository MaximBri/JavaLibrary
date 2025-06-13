package com.example.library.dao;

import com.example.library.model.Book;
import java.sql.*;
import java.util.List;

public class BookDao extends BasePublicationDao<Book> {

  @Override
  protected void createTypeSpecificTable() {
    String sql = "CREATE TABLE IF NOT EXISTS books (" +
        "id BIGINT PRIMARY KEY, " +
        "author VARCHAR(255), " +
        "isbn VARCHAR(20), " +
        "page_count INT, " + // Исправлено с pageCount на page_count
        "genre VARCHAR(100), " +
        "FOREIGN KEY (id) REFERENCES publications(id) ON DELETE CASCADE" +
        ");";
    try (Connection conn = DatabaseManager.getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при создании таблицы книг", e);
    }
  }

  @Override
  public void add(Book book) {
    try {
      // Вставляем базовую информацию о публикации
      insertBasePublication(book, "BOOK");

      // Вставляем информацию, специфичную для книги
      String sql = "INSERT INTO books (id, author, isbn, page_count, genre) VALUES (?, ?, ?, ?, ?)";
      try (Connection conn = DatabaseManager.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setLong(1, book.getId());
        ps.setString(2, book.getAuthor());
        ps.setString(3, book.getIsbn());
        ps.setInt(4, book.getPageCount());
        ps.setString(5, book.getGenre());

        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при добавлении книги", e);
    }
  }

  @Override
  public void update(Book book) {
    try {
      // Обновляем базовую информацию о публикации
      updateBasePublication(book);

      // Обновляем информацию, специфичную для книги
      String sql = "UPDATE books SET author=?, isbn=?, page_count=?, genre=? WHERE id=?";
      try (Connection conn = DatabaseManager.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, book.getAuthor());
        ps.setString(2, book.getIsbn());
        ps.setInt(3, book.getPageCount());
        ps.setString(4, book.getGenre());
        ps.setLong(5, book.getId());

        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при обновлении книги", e);
    }
  }

  @Override
  public List<Book> findAll() {
    return findByType("BOOK");
  }

  @Override
  protected Book findTypeSpecificById(Long id) {
    String sql = "SELECT p.*, b.author, b.isbn, b.page_count, b.genre " +
        "FROM publications p " +
        "JOIN books b ON p.id = b.id " +
        "WHERE p.id = ?";

    try (Connection conn = conn();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, id);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Book book = new Book();
          book.setId(rs.getLong("id"));
          book.setTitle(rs.getString("title"));
          book.setPublisher(rs.getString("publisher"));
          Date publishDate = rs.getDate("publish_date");
          if (publishDate != null) {
            book.setPublishDate(publishDate.toLocalDate());
          }
          book.setReserved(rs.getBoolean("reserved"));
          book.setAuthor(rs.getString("author"));
          book.setIsbn(rs.getString("isbn"));
          book.setPageCount(rs.getInt("page_count"));
          book.setGenre(rs.getString("genre"));

          return book;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при поиске книги по ID", e);
    }

    return null;
  }

  @Override
  public void delete(Long id) throws SQLException {
    // При удалении из publications, каскадно удаляются записи из books
    super.delete(id);
  }
}
