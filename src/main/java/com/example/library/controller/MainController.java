package com.example.library.controller;

import java.io.IOException;

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
        cell -> new ReadOnlyStringWrapper(cell.getValue().isReserved() ? "Забронирована" : "В наличии"));

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
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/book_dialog.fxml"));
      DialogPane dialogPane = loader.load();

      Dialog<ButtonType> dialog = new Dialog<>();
      dialog.setDialogPane(dialogPane);
      dialog.setTitle("Добавить книгу");

      BookController controller = loader.getController();

      dialog.setResultConverter(dialogButton -> {
        if (dialogButton != null
            && dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
          controller.save();
        }
        return null;
      });

      dialog.showAndWait();
      refreshTable();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void openReservationDialog() {
    Book selected = bookTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
      new Alert(Alert.AlertType.WARNING,
          "Сначала выберите, какую книгу хотите забронировать.",
          ButtonType.OK).showAndWait();
      return;
    }

    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/fxml/reservation_dialog.fxml"));
      DialogPane pane = loader.load();

      ReservationController controller = loader.getController();
      controller.setBookId(selected.getId());

      Dialog<ButtonType> dialog = new Dialog<>();
      dialog.setDialogPane(pane);
      dialog.setTitle("Забронировать книгу");

      // <— This is the crucial part:
      dialog.setResultConverter(bt -> {
        if (bt != null
            && bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
          controller.reserve();
        }
        return null;
      });

      dialog.showAndWait();
      refreshTable();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

}
