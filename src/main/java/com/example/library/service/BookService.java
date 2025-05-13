package com.example.library.service;

import com.example.library.dao.BookDao;
import com.example.library.dao.ReservationDao;
import com.example.library.model.Book;
import com.example.library.model.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BookService {
  private final BookDao bookDao;
  private final ReservationDao reservationDao;

  public BookService() {
    this.bookDao = new BookDao();
    this.reservationDao = new ReservationDao();
    this.bookDao.createTable();
    this.reservationDao.createTable();
  }

  public void addBook(String title, String author, String isbn) {
    Book book = new Book(title, author, isbn);
    bookDao.add(book);
  }

  public List<Book> getAllBooks() {
    return bookDao.findAll();
  }

  public boolean reserveBook(Long bookId, String customerName, LocalDate dueDate) {
    Optional<Book> opt = getAllBooks()
        .stream()
        .filter(b -> b.getId().equals(bookId) && !b.isReserved())
        .findFirst();
    if (opt.isPresent()) {
      Book book = opt.get();
      book.setReserved(true);
      bookDao.update(book);
      Reservation res = new Reservation(bookId, customerName, dueDate);
      reservationDao.add(res);
      return true;
    }
    return false;
  }

  public List<Reservation> getReservationsForBook(Long bookId) {
    return reservationDao.findByBookId(bookId);
  }

  public List<Book> getAll() {
    return bookDao.findAll();
  }
}