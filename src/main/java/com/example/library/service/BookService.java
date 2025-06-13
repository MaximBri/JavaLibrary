package com.example.library.service;

import com.example.library.dao.BookDao;
import com.example.library.dao.Dao;
import com.example.library.dao.ReservationDao;
import com.example.library.model.Book;
import com.example.library.model.Reservation;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BookService implements IBookService {
  private final Dao<Book> bookDao;
  private final ReservationDao resDao;

  public BookService(Dao<Book> bookDao, ReservationDao resDao) {
    this.bookDao = bookDao;
    this.resDao = resDao;
    this.bookDao.createTable();
    this.resDao.createTable();
  }

  public BookService() {
    this(new BookDao(), new ReservationDao());
  }

  @Override
  public void addBook(String title, String author, String isbn) {
    Book b = new Book(title, author, isbn);
    bookDao.add(b);
  }

  @Override
  public List<Book> getAllBooks() {
    return bookDao.findAll();
  }

  @Override
  public boolean reserveBook(Long bookId, String customerName, LocalDate dueDate) {
    Optional<Book> opt = getAllBooks().stream()
        .filter(b -> b.getId().equals(bookId) && !b.isReserved())
        .findFirst();

    if (opt.isEmpty()) {
      return false;
    }

    Book book = opt.get();
    if (book.isReserved()) {
      return false;
    }

    if (opt.isPresent()) {
      book.setReserved(true);
      bookDao.update(book);
      resDao.add(new Reservation(bookId, customerName, dueDate));
      return true;
    }
    return false;
  }

  @Override
  public void deleteBook(Long bookId) {
    resDao.findByBookId(bookId).forEach(r -> {
      try {
        resDao.delete(r.getId());
      } catch (SQLException e) {
        throw new RuntimeException("Ошибка при удалении резервирования: " + e.getMessage(), e);
      }
    });

    try {
      bookDao.delete(bookId);
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при удалении книги: " + e.getMessage(), e);
    }
  }
}
