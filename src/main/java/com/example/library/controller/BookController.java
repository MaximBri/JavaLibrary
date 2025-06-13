package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.service.PublicationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class BookController implements PublicationController {
  @FXML
  private TextField titleField;
  @FXML
  private TextField publisherField;
  @FXML
  private DatePicker publishDatePicker;
  @FXML
  private TextField authorField;
  @FXML
  private TextField isbnField;
  @FXML
  private TextField pageCountField;
  @FXML
  private TextField genreField;
  private final PublicationService publicationService;

  public BookController() {
    this.publicationService = new PublicationService();
  }

  @FXML
  public void initialize() {
    publishDatePicker.setValue(LocalDate.now());
  }

  @Override
  public void save() {
    try {
      String title = titleField.getText().trim();
      String publisher = publisherField.getText().trim();
      LocalDate publishDate = publishDatePicker.getValue();
      String author = authorField.getText().trim();
      String isbn = isbnField.getText().trim();
      int pageCount = Integer.parseInt(pageCountField.getText().trim());
      String genre = genreField.getText().trim();

      if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
        showError("Поля Название, Автор и ISBN должны быть заполнены.");
        return;
      }

      Book book = new Book();
      book.setTitle(title);
      book.setPublisher(publisher);
      book.setPublishDate(publishDate);
      book.setAuthor(author);
      book.setIsbn(isbn);
      book.setPageCount(pageCount);
      book.setGenre(genre);

      publicationService.addBook(book);
    } catch (NumberFormatException e) {
      showError("Пожалуйста, введите корректное число страниц.");
    } catch (Exception e) {
      showError("Ошибка при добавлении книги: " + e.getMessage());
      e.printStackTrace(); // Добавьте логирование для отладки
    }
  }

  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Ошибка");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
