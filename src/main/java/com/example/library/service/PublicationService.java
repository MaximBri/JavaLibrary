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

    // Инициализация таблиц
    this.bookDao.createTable();
    this.magazineDao.createTable();
    this.newspaperDao.createTable();
    this.ebookDao.createTable();
    this.reservationDao.createTable();
  }

  // Метод для получения всех публикаций
  public List<Publication> getAllPublications() {
    List<Publication> publications = new ArrayList<>();
    publications.addAll(bookDao.findAll());
    publications.addAll(magazineDao.findAll());
    publications.addAll(newspaperDao.findAll());
    publications.addAll(ebookDao.findAll());
    return publications;
  }

  // Метод для поиска публикаций по названию
  public List<Publication> findPublicationsByTitle(String title) {
    List<Publication> publications = new ArrayList<>();
    publications.addAll(bookDao.findByTitle(title));
    publications.addAll(magazineDao.findByTitle(title));
    publications.addAll(newspaperDao.findByTitle(title));
    publications.addAll(ebookDao.findByTitle(title));
    return publications;
  }

  // Метод для поиска публикаций по издателю
  public List<Publication> findPublicationsByPublisher(String publisher) {
    List<Publication> publications = new ArrayList<>();
    publications.addAll(bookDao.findByPublisher(publisher));
    publications.addAll(magazineDao.findByPublisher(publisher));
    publications.addAll(newspaperDao.findByPublisher(publisher));
    publications.addAll(ebookDao.findByPublisher(publisher));
    return publications;
  }

  // Методы для работы с книгами
  public void addBook(Book book) {
    bookDao.add(book);
  }

  public void updateBook(Book book) {
    bookDao.update(book);
  }

  public List<Book> getAllBooks() {
    return bookDao.findAll();
  }

  // Методы для работы с журналами
  public void addMagazine(Magazine magazine) {
    magazineDao.add(magazine);
  }

  public void updateMagazine(Magazine magazine) {
    magazineDao.update(magazine);
  }

  public List<Magazine> getAllMagazines() {
    return magazineDao.findAll();
  }

  // Методы для работы с газетами
  public void addNewspaper(Newspaper newspaper) {
    newspaperDao.add(newspaper);
  }

  public void updateNewspaper(Newspaper newspaper) {
    newspaperDao.update(newspaper);
  }

  public List<Newspaper> getAllNewspapers() {
    return newspaperDao.findAll();
  }

  // Методы для работы с электронными книгами
  public void addEBook(EBook ebook) {
    ebookDao.add(ebook);
  }

  public void updateEBook(EBook ebook) {
    ebookDao.update(ebook);
  }

  public List<EBook> getAllEBooks() {
    return ebookDao.findAll();
  }

  public void deletePublication(Publication publication) {
    if (publication == null) {
      throw new IllegalArgumentException("Публикация не может быть null");
    }

    try {
      if (publication instanceof Book) {
        bookDao.delete(publication.getId());
      } else if (publication instanceof Magazine) {
        magazineDao.delete(publication.getId());
      } else if (publication instanceof Newspaper) {
        newspaperDao.delete(publication.getId());
      } else if (publication instanceof EBook) {
        ebookDao.delete(publication.getId());
      } else {
        throw new IllegalArgumentException("Неизвестный тип публикации: " +
            publication.getClass().getSimpleName());
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при удалении публикации: " + e.getMessage(), e);
    }
  }

  public void deletePublication(Long id, String type) {
    Dao<?> dao = getDaoByType(type);
    try {
      dao.delete(id);
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при удалении публикации: " + e.getMessage(), e);
    }
  }

  private Dao<?> getDaoByType(String type) {
    switch (type) {
      case "Book":
      case "Книга":
        return bookDao;
      case "Magazine":
      case "Журнал":
        return magazineDao;
      case "Newspaper":
      case "Газета":
        return newspaperDao;
      case "EBook":
      case "Эл. книга":
        return ebookDao;
      default:
        throw new IllegalArgumentException("Неизвестный тип публикации: " + type);
    }
  }

  // Метод для бронирования публикации
  public boolean reservePublication(Long publicationId, String customerName, LocalDate dueDate) {
    List<Publication> allPublications = getAllPublications();
    Optional<Publication> optionalPublication = allPublications.stream()
        .filter(p -> p.getId().equals(publicationId) && !p.isReserved())
        .findFirst();

    if (optionalPublication.isPresent()) {
      Publication publication = optionalPublication.get();
      publication.setReserved(true);

      // Обновляем публикацию в соответствующей таблице
      if (publication instanceof Book && !(publication instanceof EBook)) {
        bookDao.update((Book) publication);
      } else if (publication instanceof Magazine) {
        magazineDao.update((Magazine) publication);
      } else if (publication instanceof Newspaper) {
        newspaperDao.update((Newspaper) publication);
      } else if (publication instanceof EBook) {
        ebookDao.update((EBook) publication);
      }

      // Создаем запись о бронировании
      reservationDao.add(new Reservation(publicationId, customerName, dueDate));
      return true;
    }
    return false;
  }

  // Метод для отмены бронирования
  public boolean cancelReservation(Long publicationId) {
    List<Reservation> reservations = reservationDao.findByBookId(publicationId);
    if (reservations.isEmpty()) {
      return false;
    }

    // Находим публикацию и снимаем флаг бронирования
    List<Publication> allPublications = getAllPublications();
    Optional<Publication> optionalPublication = allPublications.stream()
        .filter(p -> p.getId().equals(publicationId))
        .findFirst();

    if (optionalPublication.isPresent()) {
      Publication publication = optionalPublication.get();
      publication.setReserved(false);

      // Обновляем публикацию в соответствующей таблице
      if (publication instanceof Book && !(publication instanceof EBook)) {
        bookDao.update((Book) publication);
      } else if (publication instanceof Magazine) {
        magazineDao.update((Magazine) publication);
      } else if (publication instanceof Newspaper) {
        newspaperDao.update((Newspaper) publication);
      } else if (publication instanceof EBook) {
        ebookDao.update((EBook) publication);
      }

      // Удаляем записи о бронировании
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

  // Метод для отмены просроченных бронирований
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
