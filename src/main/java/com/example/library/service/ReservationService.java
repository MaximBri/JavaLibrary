package com.example.library.service;

import com.example.library.dao.BookDao;
import com.example.library.dao.ReservationDao;
import com.example.library.model.Book;
import com.example.library.model.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ReservationService {
  private final BookDao bookDao;
  private final ReservationDao reservationDao;

  public ReservationService() {
    this.bookDao = new BookDao();
    this.reservationDao = new ReservationDao();
  }

  /**
   * Создает новое бронирование книги.
   */
  public boolean createReservation(Long bookId, String customerName, LocalDate dueDate) {
    Optional<Book> optionalBook = bookDao.findAll().stream()
        .filter(b -> b.getId().equals(bookId) && !b.isReserved())
        .findFirst();
    if (optionalBook.isPresent()) {
      Book book = optionalBook.get();
      book.setReserved(true);
      bookDao.update(book);
      Reservation reservation = new Reservation(bookId, customerName, dueDate);
      reservationDao.add(reservation);
      return true;
    }
    return false;
  }

  /**
   * Отменяет бронь (при возврате книги).
   */
  public boolean cancelReservation(Long reservationId) {
    // находим бронь
    List<Reservation> all = reservationDao.findByBookId(null);
    Optional<Reservation> opt = all.stream()
        .filter(r -> r.getId().equals(reservationId))
        .findFirst();
    if (opt.isPresent()) {
      Reservation res = opt.get();
      // снимаем флаг брони у книги
      List<Book> books = bookDao.findAll();
      books.stream()
          .filter(b -> b.getId().equals(res.getBookId()))
          .findFirst()
          .ifPresent(book -> {
            book.setReserved(false);
            bookDao.update(book);
          });
      // удаляем запись о брони
      reservationDao.delete(reservationId);
      return true;
    }
    return false;
  }

  /**
   * Возвращает список всех бронирований или по книге.
   */
  public List<Reservation> getAllReservations() {
    return reservationDao.findByBookId(null);
  }

  public List<Reservation> getReservationsForBook(Long bookId) {
    return reservationDao.findByBookId(bookId);
  }
}
