package com.example.library.controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.example.library.model.Book;
import com.example.library.model.Reservation;
import com.example.library.service.BookService;
import com.example.library.service.ReservationService;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
  private TableColumn<Book, Void> colAction;

  @FXML
  private Button addBookBtn;
  @FXML
  private Button reserveBtn;
  @FXML
  private Button deleteBtn;
  @FXML
  private Label countLabel;

  private final BookService bookService;
  private final ReservationService reservationService;

  public MainController() {
    this.bookService = new BookService();
    this.reservationService = new ReservationService();
  }

  @FXML
  public void initialize() {
    bindColumnWidths();
    setupColumns();
    setupRowHighlighting();
    setupActionColumn();
    setupButtons();

    reservationService.cancelExpiredReservations(); // снятие просроченных броней при старте
    loadBooks();
  }

  private void bindColumnWidths() {
    DoubleBinding totalWidth = bookTable.widthProperty().subtract(2);
    colId.prefWidthProperty().bind(totalWidth.multiply(0.10));
    colTitle.prefWidthProperty().bind(totalWidth.multiply(0.30));
    colAction.prefWidthProperty().bind(totalWidth.multiply(0.10));
    double remaining = 0.50;
    int dynamicCols = 3;
    double each = remaining / dynamicCols;
    colAuthor.prefWidthProperty().bind(totalWidth.multiply(each));
    colIsbn.prefWidthProperty().bind(totalWidth.multiply(each));
    colStatus.prefWidthProperty().bind(totalWidth.multiply(each));
  }

  private void setupColumns() {
    colId.setCellValueFactory(new PropertyValueFactory<>("id"));
    colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
    colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
    colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
    colStatus.setCellValueFactory(cell -> {
      Book book = cell.getValue();
      if (!book.isReserved()) {
        return new ReadOnlyStringWrapper("В наличии");
      }
      List<Reservation> reservations = reservationService.getReservationsForBook(book.getId());
      Optional<Reservation> last = reservations.stream()
          .max(Comparator.comparing(Reservation::getDueDate));
      return last.map(r -> new ReadOnlyStringWrapper(
          String.format("Забронирована: %s до %s", r.getCustomerName(), r.getDueDate())))
          .orElseGet(() -> new ReadOnlyStringWrapper("Забронирована"));
    });
  }

  private void setupRowHighlighting() {
    bookTable.setRowFactory(table -> new TableRow<>() {
      @Override
      protected void updateItem(Book book, boolean empty) {
        super.updateItem(book, empty);
        if (empty || book == null) {
          setStyle("");
        } else if (book.isReserved()) {
          setStyle("-fx-background-color: #ffdddd;");
        } else {
          setStyle("-fx-background-color:rgb(66, 94, 252);");
        }
      }
    });
  }

  private void setupActionColumn() {
    colAction.setCellFactory(col -> new TableCell<>() {
      private final Button returnBtn = new Button("Вернуть");

      {
        returnBtn.getStyleClass().add("toolbar-button");
        returnBtn.setOnAction(e -> handleReturn(getCurrentBook().getId()));
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null);
        } else {
          Book book = getCurrentBook();
          setGraphic(book != null && book.isReserved() ? returnBtn : null);
        }
      }

      private Book getCurrentBook() {
        return getTableView().getItems().get(getIndex());
      }
    });
  }

  private void setupButtons() {
    addBookBtn.setOnAction(e -> openBookDialog());
    reserveBtn.setOnAction(e -> openReservationDialog());
    deleteBtn.setOnAction(e -> handleDelete());
  }

  private void loadBooks() {
    List<Book> books = bookService.getAllBooks();
    bookTable.getItems().setAll(books);
    countLabel.setText("Всего книг: " + books.size());
  }

  private void refreshTable() {
    reservationService.cancelExpiredReservations();
    loadBooks();
  }

  private void openBookDialog() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/book_dialog.fxml"));
      DialogPane pane = loader.load();
      Dialog<ButtonType> dialog = new Dialog<>();
      dialog.setDialogPane(pane);
      dialog.setTitle("Добавить книгу");
      BookController ctrl = loader.getController();
      dialog.setResultConverter(bt -> {
        if (bt != null && bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
          ctrl.save();
        }
        return null;
      });
      dialog.showAndWait();
      refreshTable();
    } catch (IOException ex) {
      showError("Не удалось открыть окно добавления книги", ex);
    }
  }

  private void openReservationDialog() {
    Book book = bookTable.getSelectionModel().getSelectedItem();
    if (book == null) {
      showWarning("Сначала выберите книгу для бронирования.");
      return;
    }
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reservation_dialog.fxml"));
      DialogPane pane = loader.load();
      ReservationController ctrl = loader.getController();
      ctrl.setBookId(book.getId());
      Dialog<ButtonType> dialog = new Dialog<>();
      dialog.setDialogPane(pane);
      dialog.setTitle("Забронировать книгу");
      dialog.setResultConverter(bt -> {
        if (bt != null && bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
          ctrl.reserve();
        }
        return null;
      });
      dialog.showAndWait();
      refreshTable();
    } catch (IOException ex) {
      showError("Не удалось открыть окно бронирования", ex);
    }
  }

  private void handleDelete() {
    Book book = bookTable.getSelectionModel().getSelectedItem();
    if (book == null) {
      showWarning("Сначала выберите книгу для удаления.");
      return;
    }
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
        String.format("Удалить книгу '%s'?", book.getTitle()), ButtonType.YES, ButtonType.NO);
    Optional<ButtonType> result = confirm.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.YES) {
      bookService.deleteBook(book.getId());
      refreshTable();
    }
  }

  public void handleReturn(Long bookId) {
    Long reservationId = reservationService.listAll().stream()
        .filter(r -> r.getBookId().equals(bookId))
        .map(Reservation::getId)
        .findFirst()
        .orElse(null);
    if (reservationId == null) {
      showWarning("Книга не забронирована.");
      return;
    }
    boolean success = reservationService.cancel(reservationId);
    if (success) {
      showInfo("Бронирование успешно отменено.");
      refreshTable();
    }
  }

  private void showError(String msg, Exception ex) {
    ex.printStackTrace();
    new Alert(Alert.AlertType.ERROR, msg + "\n" + ex.getMessage(), ButtonType.OK).showAndWait();
  }

  private void showWarning(String msg) {
    new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
  }

  private void showInfo(String msg) {
    new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
  }
}
