package com.example.library.controller;

import com.example.library.service.BookService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
    if (okButton != null) {
      okButton.setOnAction(event -> {
        String title = titleField.getText();
        String author = authorField.getText();
        String isbn = isbnField.getText();
        System.out.println("Добавляем книгу: " + title + " - " + author + " - " + isbn);
      });
    } else {
      System.err.println("Кнопка OK не найдена.");
    }
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
