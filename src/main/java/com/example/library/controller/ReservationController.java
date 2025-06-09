package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.service.BookService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.time.LocalDate;

public class ReservationController {

  @FXML
  private TextField nameField;
  @FXML
  private DatePicker datePicker;
  @FXML
  private Label bookLabel;

  // Мы можем либо передать BookService в контроллер через MainController,
  // либо завести здесь новый экземпляр
  private final BookService bookService = new BookService();

  // Поле, куда MainController запишет ID книги перед открытием диалога
  private long bookId;

  public void setBookId(long bookId) {
    this.bookId = bookId;

    Book book = bookService.getAllBooks().stream()
        .filter(b -> b.getId() == bookId)
        .findFirst()
        .orElse(null);

    if (book != null) {
      bookLabel.setText("Бронируемая книга: " + book.getTitle() + ", автор - " + book.getAuthor());
    } else {
      bookLabel.setText("Неизвестная книга");
    }
  }

  public void reserve() {
    String customer = nameField.getText().trim();
    LocalDate dueDate = datePicker.getValue();

    if (customer.isEmpty() || dueDate == null) {
      return;
    }

    boolean ok = bookService.reserveBook(bookId, customer, dueDate);
    if (!ok) {
      Alert err = new Alert(Alert.AlertType.ERROR,
          "Эта книга уже забронирована или не найдена.", ButtonType.OK);
      err.showAndWait();
      return;
    }

    // Закрытие диалога
    DialogPane pane = (DialogPane) nameField.getScene().getRoot();
    pane.getButtonTypes().stream()
        .filter(bt -> bt.getButtonData() == ButtonType.OK.getButtonData())
        .findFirst()
        .ifPresent(bt -> pane.lookupButton(bt).getScene().getWindow().hide());
  }
}
