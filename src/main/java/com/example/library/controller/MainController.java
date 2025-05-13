package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.service.BookService;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.StageStyle;

public class MainController {

  @FXML
  private TableView<Book> bookTable;
  @FXML
  private TableColumn<Book, Long> colId;
  @FXML
  private TableColumn<Book, String> colTitle;
  @FXML
  private TableColumn<Book, String> colAuthor;
  @FXML
  private TableColumn<Book, String> colIsbn;
  @FXML
  private TableColumn<Book, String> colStatus;
  @FXML
  private Button addBookBtn;
  @FXML
  private Button reserveBtn;

  private final BookService bookService = new BookService();

  @FXML
  public void initialize() {
    // Настраиваем колонки
    colId.setCellValueFactory(new PropertyValueFactory<>("id"));
    colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
    colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
    colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
    colStatus.setCellValueFactory(
        cell -> new ReadOnlyStringWrapper(cell.getValue().isReserved() ? "Reserved" : "Available"));

    // Загружаем данные
    refreshTable();

    // Обработчики кнопок
    addBookBtn.setOnAction(e -> openBookDialog());
    reserveBtn.setOnAction(e -> openReservationDialog());
  }

  private void refreshTable() {
    bookTable.getItems().setAll(bookService.getAllBooks());
  }

  private void openBookDialog() {
    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/fxml/book_dialog.fxml"));
      Parent root = loader.load();

      Dialog<Void> dialog = new Dialog<>();
      dialog.initStyle(StageStyle.UTILITY);
      dialog.setTitle("Add New Book");
      dialog.getDialogPane().setContent(root);
      dialog.getDialogPane().getButtonTypes().addAll(
          ButtonType.OK, ButtonType.CANCEL);

      // По нажатию OK внутри контроллера вызывается save()
      dialog.showAndWait();
      refreshTable();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  // private void openReservationDialog() {
  // Book selected = bookTable.getSelectionModel().getSelectedItem();
  // if (selected == null) {
  // Alert alert = new Alert(Alert.AlertType.WARNING,
  // "Please select a book to reserve.", ButtonType.OK);
  // alert.showAndWait();
  // return;
  // }

  // try {
  // FXMLLoader loader = new FXMLLoader(
  // getClass().getResource("/fxml/reservation_dialog.fxml"));
  // Parent root = loader.load();

  // // Передаем ID книги в контроллер бронирования
  // ReservationController ctrl = loader.getController();
  // ctrl.setBookId(selected.getId());

  // Dialog<Void> dialog = new Dialog<>();
  // dialog.initStyle(StageStyle.UTILITY);
  // dialog.setTitle("Reserve Book");
  // dialog.getDialogPane().setContent(root);

  // dialog.showAndWait();
  // refreshTable();
  // } catch (Exception ex) {
  // ex.printStackTrace();
  // }
  // }
  private void openReservationDialog() {
    Book selected = bookTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
      Alert alert = new Alert(Alert.AlertType.WARNING,
          "Please select a book to reserve.", ButtonType.OK);
      alert.showAndWait();
      return;
    }

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reservation_dialog.fxml"));
      DialogPane pane = loader.load();

      ReservationController controller = loader.getController();
      controller.setBookId(selected.getId());

      Dialog<ButtonType> dialog = new Dialog<>();
      dialog.setDialogPane(pane);
      dialog.setTitle("Reserve Book");

      dialog.showAndWait(); 
      refreshTable();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
