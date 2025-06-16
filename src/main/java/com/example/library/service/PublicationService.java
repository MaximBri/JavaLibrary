package com.example.library.service;

import com.example.library.dao.*;
import com.example.library.model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PublicationService {
  private final BookDao bookDao;
  private final MagazineDao magazineDao;
  private final NewspaperDao newspaperDao;
  private final EBookDao ebookDao;
  private final ReservationDao reservationDao;

  public PublicationService() {
    this.bookDao = new BookDao();
    this.magazineDao = new MagazineDao();
    this.newspaperDao = new NewspaperDao();
    this.ebookDao = new EBookDao();
    this.reservationDao = new ReservationDao();

    this.bookDao.createTable();
    this.magazineDao.createTable();
    this.newspaperDao.createTable();
    this.ebookDao.createTable();
    this.reservationDao.createTable();
  }

  public List<Publication> getAllPublications() {
    List<Publication> publications = new ArrayList<>();
    publications.addAll(bookDao.findAll());
    publications.addAll(magazineDao.findAll());
    publications.addAll(newspaperDao.findAll());
    publications.addAll(ebookDao.findAll());
    return publications;
  }

  public List<Publication> findPublicationsByTitle(String title) {
    List<Publication> publications = new ArrayList<>();
    publications.addAll(bookDao.findByTitle(title));
    publications.addAll(magazineDao.findByTitle(title));
    publications.addAll(newspaperDao.findByTitle(title));
    publications.addAll(ebookDao.findByTitle(title));
    return publications;
  }

  public List<Publication> findPublicationsByPublisher(String publisher) {
    List<Publication> publications = new ArrayList<>();
    publications.addAll(bookDao.findByPublisher(publisher));
    publications.addAll(magazineDao.findByPublisher(publisher));
    publications.addAll(newspaperDao.findByPublisher(publisher));
    publications.addAll(ebookDao.findByPublisher(publisher));
    return publications;
  }

  public void addBook(Book book) {
    bookDao.add(book);
  }

  public void updateBook(Book book) {
    bookDao.update(book);
  }

  public List<Book> getAllBooks() {
    return bookDao.findAll();
  }

  public void addMagazine(Magazine magazine) {
    magazineDao.add(magazine);
  }

  public void updateMagazine(Magazine magazine) {
    magazineDao.update(magazine);
  }

  public List<Magazine> getAllMagazines() {
    return magazineDao.findAll();
  }

  public void addNewspaper(Newspaper newspaper) {
    newspaperDao.add(newspaper);
  }

  public void updateNewspaper(Newspaper newspaper) {
    newspaperDao.update(newspaper);
  }

  public List<Newspaper> getAllNewspapers() {
    return newspaperDao.findAll();
  }

  public void addEBook(EBook ebook) {
    ebookDao.add(ebook);
  }

  public void updateEBook(EBook ebook) {
    ebookDao.update(ebook);
  }

  public List<EBook> getAllEBooks() {
    return ebookDao.findAll();
  }

  public boolean deletePublication(Publication pub) {
    try {
      getDaoByType(pub).delete(pub.getId());
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private PublicationDao getDaoByType(Publication pub) {
    if (pub instanceof EBook)
      return ebookDao;
    if (pub instanceof Newspaper)
      return newspaperDao;
    if (pub instanceof Magazine)
      return magazineDao;
    if (pub instanceof Book)
      return bookDao;
    throw new IllegalArgumentException("Неизвестный тип публикации: "
        + pub.getClass().getSimpleName());
  }

  public boolean reservePublication(Long publicationId, String userLogin, LocalDate dueDate) {
    List<Reservation> existing = reservationDao.findByPublicationId(publicationId);
    boolean already = existing.stream().anyMatch(r -> !r.getDueDate().isBefore(LocalDate.now()));
    if (already) {
      return false;
    }
    try {
      reservationDao.add(new Reservation(null, publicationId, userLogin, dueDate));
    } catch (Exception e) {
      return false;
    }
    Optional<Publication> opt = getAllPublications().stream()
        .filter(p -> p.getId().equals(publicationId))
        .findFirst();
    if (opt.isPresent()) {
      Publication p = opt.get();
      p.setReserved(true);
      getDaoByType(p).update(p);
    }
    return true;
  }

  public boolean reservePublication(Publication pub, String userLogin, LocalDate dueDate) {
    try {
      reservationDao.add(new Reservation(null, pub.getId(), userLogin, dueDate));
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean cancelReservation(Long publicationId) {
    List<Reservation> reservations = reservationDao.findByPublicationId(publicationId);
    if (reservations.isEmpty()) {
      return false;
    }

    List<Publication> allPublications = getAllPublications();
    Optional<Publication> optionalPublication = allPublications.stream()
        .filter(p -> p.getId().equals(publicationId))
        .findFirst();

    if (optionalPublication.isPresent()) {
      Publication publication = optionalPublication.get();
      publication.setReserved(false);

      if (publication instanceof Book && !(publication instanceof EBook)) {
        bookDao.update((Book) publication);
      } else if (publication instanceof Magazine) {
        magazineDao.update((Magazine) publication);
      } else if (publication instanceof Newspaper) {
        newspaperDao.update((Newspaper) publication);
      } else if (publication instanceof EBook) {
        ebookDao.update((EBook) publication);
      }

      for (Reservation reservation : reservations) {
        try {
          reservationDao.delete(reservation.getId());
        } catch (SQLException e) {
          throw new RuntimeException("Ошибка при удалении резервирования: " + e.getMessage(), e);
        }
      }
      return true;
    }
    return false;
  }

  public void cancelExpiredReservations() {
    List<Reservation> allReservations = reservationDao.findAll();
    List<Reservation> expiredReservations = allReservations.stream()
        .filter(r -> r.getDueDate().isBefore(LocalDate.now()))
        .collect(Collectors.toList());

    for (Reservation reservation : expiredReservations) {
      cancelReservation(reservation.getBookId());
    }

    if (!expiredReservations.isEmpty()) {
      System.out.println("Удалено просроченных бронирований: " + expiredReservations.size());
    }
  }
}
