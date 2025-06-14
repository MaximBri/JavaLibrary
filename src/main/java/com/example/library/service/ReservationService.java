package com.example.library.service;

import com.example.library.dao.ReservationDao;
import com.example.library.model.Reservation;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ReservationService {
  private final ReservationDao reservationDao;

  public ReservationService() {
    this.reservationDao = new ReservationDao();
    this.reservationDao.createTable();
  }

  public List<Reservation> getAllReservations() {
    return reservationDao.findAll();
  }

  public List<Reservation> getReservationsForBook(Long bookId) {
    return reservationDao.findByBookId(bookId);
  }

  public void addReservation(Reservation reservation) {
    reservationDao.add(reservation);
  }

  public void cancelReservation(Long id) throws SQLException {
    reservationDao.delete(id);
  }

  public void cancelExpiredReservations() {
    List<Reservation> allReservations = reservationDao.findAll();
    for (Reservation reservation : allReservations) {
      if (reservation.getDueDate().isBefore(LocalDate.now())) {
        try {
          reservationDao.delete(reservation.getId());
        } catch (SQLException e) {
          throw new RuntimeException("Ошибка при удалении резерваций: " + e.getMessage(), e);
        }
      }
    }
  }

  public Optional<String> getReservedBy(Long bookId) {
    List<Reservation> list = getReservationsForBook(bookId);
    return list.stream()
        .filter(r -> !r.getDueDate().isBefore(LocalDate.now()))
        .findFirst()
        .map(Reservation::getCustomerName);
  }
}
