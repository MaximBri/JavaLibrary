package com.example.library.controller;

import com.example.library.service.BookService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;

public class BookController {

  @FXML
  private TextField titleField;
  @FXML
  private TextField authorField;
  @FXML
  private TextField isbnField;
  @FXML
  private DialogPane dialogPane;

  @FXML
  public void initialize() {
    dialogPane.lookupButton(ButtonType.OK).addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
      String title = titleField.getText().trim();
      String author = authorField.getText().trim();
      String isbn = isbnField.getText().trim();

      if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
        // Показать alert
        Alert alert = new Alert(Alert.AlertType.WARNING, "All fields are required.");
        alert.showAndWait();
        event.consume(); // отменить закрытие диалога
        return;
      }

      bookService.addBook(title, author, isbn);
      // диалог сам закроется, если не вызван event.consume()
    });
  }

  // Сервис для работы с книгами
  private final BookService bookService = new BookService();

  /**
   * Вызывается MainController через .save()
   * Здесь мы читаем поля, добавляем книгу и закрываем диалог.
   */
  public void save() {
    String title = titleField.getText().trim();
    String author = authorField.getText().trim();
    String isbn = isbnField.getText().trim();

    if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
      // Можно выбросить исключение или показать предупреждение
      return;
    }

    bookService.addBook(title, author, isbn);

    // Закрываем диалог: получаем DialogPane из одного из компонентов
    DialogPane pane = (DialogPane) titleField.getScene().getRoot();
    pane.getButtonTypes().stream()
        .filter(bt -> bt.getButtonData() == ButtonType.OK.getButtonData())
        .findFirst()
        .ifPresent(bt -> pane.lookupButton(bt).getScene().getWindow().hide());
  }
}
