package com.example.library.dao;

import com.example.library.model.EBook;
import java.sql.*;
import java.util.List;

public class EBookDao extends BasePublicationDao<EBook> {

  @Override
  protected void createTypeSpecificTable() {
    // Сначала создаем таблицу для книг, если ее еще нет
    new BookDao().createTypeSpecificTable();

    String sql = "CREATE TABLE IF NOT EXISTS ebooks (" +
        "id BIGINT PRIMARY KEY, " +
        "format VARCHAR(20), " +
        "file_size_mb DOUBLE, " +
        "download_url VARCHAR(255), " +
        "is_drm_protected BOOLEAN, " +
        "FOREIGN KEY (id) REFERENCES books(id) ON DELETE CASCADE" +
        ");";
    try (Connection conn = DatabaseManager.getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при создании таблицы электронных книг", e);
    }
  }

  @Override
  public void add(EBook ebook) {
    try {
      // Вставляем базовую информацию о публикации
      insertBasePublication(ebook, "EBOOK");

      // Вставляем информацию о книге
      String bookSql = "INSERT INTO books (id, author, isbn, page_count, genre) VALUES (?, ?, ?, ?, ?)";
      try (Connection conn = DatabaseManager.getConnection();
          PreparedStatement ps = conn.prepareStatement(bookSql)) {
        ps.setLong(1, ebook.getId());
        ps.setString(2, ebook.getAuthor());
        ps.setString(3, ebook.getIsbn());
        ps.setInt(4, ebook.getPageCount());
        ps.setString(5, ebook.getGenre());
        ps.executeUpdate();
      }

      // Вставляем информацию, специфичную для электронной книги
      String ebookSql = "INSERT INTO ebooks (id, format, file_size_mb, download_url, is_drm_protected) VALUES (?, ?, ?, ?, ?)";
      try (Connection conn = DatabaseManager.getConnection();
          PreparedStatement ps = conn.prepareStatement(ebookSql)) {
        ps.setLong(1, ebook.getId());
        ps.setString(2, ebook.getFormat());
        ps.setDouble(3, ebook.getFileSizeMB());
        ps.setString(4, ebook.getDownloadUrl());
        ps.setBoolean(5, ebook.isDrmProtected());
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при добавлении электронной книги", e);
    }
  }

  @Override
  public void update(EBook ebook) {
    try {
      // Обновляем базовую информацию о публикации
      updateBasePublication(ebook);

      // Обновляем информацию о книге
      String bookSql = "UPDATE books SET author=?, isbn=?, page_count=?, genre=? WHERE id=?";
      try (Connection conn = DatabaseManager.getConnection();
          PreparedStatement ps = conn.prepareStatement(bookSql)) {
        ps.setString(1, ebook.getAuthor());
        ps.setString(2, ebook.getIsbn());
        ps.setInt(3, ebook.getPageCount());
        ps.setString(4, ebook.getGenre());
        ps.setLong(5, ebook.getId());
        ps.executeUpdate();
      }

      // Обновляем информацию, специфичную для электронной книги
      String ebookSql = "UPDATE ebooks SET format=?, file_size_mb=?, download_url=?, is_drm_protected=? WHERE id=?";
      try (Connection conn = DatabaseManager.getConnection();
          PreparedStatement ps = conn.prepareStatement(ebookSql)) {
        ps.setString(1, ebook.getFormat());
        ps.setDouble(2, ebook.getFileSizeMB());
        ps.setString(3, ebook.getDownloadUrl());
        ps.setBoolean(4, ebook.isDrmProtected());
        ps.setLong(5, ebook.getId());
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при обновлении электронной книги", e);
    }
  }

  @Override
  public List<EBook> findAll() {
    return findByType("EBOOK");
  }

  @Override
  protected EBook findTypeSpecificById(Long id) {
    String sql = "SELECT p.*, b.author, b.isbn, b.page_count, b.genre, " +
        "e.format, e.file_size_mb, e.download_url, e.is_drm_protected " +
        "FROM publications p " +
        "JOIN books b ON p.id = b.id " +
        "JOIN ebooks e ON b.id = e.id " +
        "WHERE p.id = ?";
    try (Connection conn = conn();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          EBook ebook = new EBook();
          ebook.setId(rs.getLong("id"));
          ebook.setTitle(rs.getString("title"));
          ebook.setPublisher(rs.getString("publisher"));
          Date publishDate = rs.getDate("publish_date");
          if (publishDate != null) {
            ebook.setPublishDate(publishDate.toLocalDate());
          }
          ebook.setReserved(rs.getBoolean("reserved"));
          ebook.setAuthor(rs.getString("author"));
          ebook.setIsbn(rs.getString("isbn"));
          ebook.setPageCount(rs.getInt("page_count"));
          ebook.setGenre(rs.getString("genre"));
          ebook.setFormat(rs.getString("format"));
          ebook.setFileSizeMB(rs.getDouble("file_size_mb"));
          ebook.setDownloadUrl(rs.getString("download_url"));
          ebook.setDrmProtected(rs.getBoolean("is_drm_protected"));
          return ebook;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при поиске электронной книги по ID", e);
    }
    return null;
  }

  @Override
  public void delete(Long id) throws SQLException {
    // При удалении из publications, каскадно удаляются записи из books и ebooks
    super.delete(id);
  }
}
