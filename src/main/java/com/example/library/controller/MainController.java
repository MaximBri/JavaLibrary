package com.example.library.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.library.dao.BookDao;
import com.example.library.dao.ReservationDao;
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
  private Button addBookBtn, reserveBtn, deleteBtn;
  @FXML
  private Label countLabel;

  private final BookService bookService = new BookService(new BookDao(), new ReservationDao());
  private final ReservationService reservationService = new ReservationService();
  private static MainController instance;

  public MainController() {
    instance = this;
  }

  public static MainController getInstance() {
    return instance;
  }

  @FXML
  public void initialize() {
    // 1) Настраиваем колонки модели Book
    colId.setCellValueFactory(new PropertyValueFactory<>("id"));
    colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
    colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
    colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

    DoubleBinding tableWidth = bookTable.widthProperty()
        .subtract(2);
    colId.prefWidthProperty().bind(tableWidth.multiply(0.10));
    colTitle.prefWidthProperty().bind(tableWidth.multiply(0.30));
    colAction.prefWidthProperty().bind(tableWidth.multiply(0.10));
    double remaining = 0.50;
    int otherCols = 3;
    double each = remaining / otherCols;
    colAuthor.prefWidthProperty().bind(tableWidth.multiply(each));
    colIsbn.prefWidthProperty().bind(tableWidth.multiply(each));
    colStatus.prefWidthProperty().bind(tableWidth.multiply(each));

    reservationService.cancelExpiredReservations(); // удаление просроченых броней

    bookTable.setRowFactory(table -> new TableRow<>() {
      @Override
      protected void updateItem(Book book, boolean empty) {
        super.updateItem(book, empty);

        if (empty || book == null) {
          setStyle("");
        } else if (book.isReserved()) {
          // Светло-красный фон
          setStyle("-fx-background-color: #ffdddd;");
        } else {
          // Обычный фон
          setStyle("-fx-background-color:rgb(93, 136, 253);");
        }
      }
    });

    // 2) Столбец «Статус» с подробной информацией о резерве
    colStatus.setCellValueFactory(cell -> {
      Book b = cell.getValue();
      if (!b.isReserved()) {
        return new ReadOnlyStringWrapper("В наличии");
      }
      // если зарезервирована — забираем последнюю бронь
      List<Reservation> list = new ReservationDao().findByBookId(b.getId());
      Reservation last = list.stream()
          .max(Comparator.comparing(Reservation::getDueDate))
          .orElse(null);
      if (last != null) {
        String txt = String.format("Забронирована: %s до %s",
            last.getCustomerName(),
            last.getDueDate());
        return new ReadOnlyStringWrapper(txt);
      } else {
        return new ReadOnlyStringWrapper("Забронирована");
      }
    });

    deleteBtn.setOnAction(e -> {
      Book sel = bookTable.getSelectionModel().getSelectedItem();
      if (sel == null) {
        new Alert(Alert.AlertType.WARNING,
            "Сначала выберите книгу для удаления.", ButtonType.OK).showAndWait();
        return;
      }
      // Подтверждение
      Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
          "Удалить книгу \"" + sel.getTitle() + "\"?", ButtonType.YES, ButtonType.NO);
      Optional<ButtonType> res = confirm.showAndWait();
      if (res.isPresent() && res.get() == ButtonType.YES) {
        bookService.deleteBook(sel.getId());
        refreshTable();
      }
    });

    // 3) Колонка «Действия» с кнопкой «Вернуть»
    // Обратите внимание: <TableColumn fx:id="colAction"/> должен быть объявлен в
    // FXML
    colAction.setCellFactory(col -> new TableCell<>() {
      private final Button btn = new Button("Вернуть");

      {
        btn.setOnAction(e -> {
          Book book = getTableView().getItems().get(getIndex());
          handleReturn(book.getId());
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
          // сама строка пуста — не рисуем ничего
          setGraphic(null);
        } else {
          Book book = getTableView().getItems().get(getIndex());
          if (book.isReserved()) {
            // только если книга зарезервирована — показываем кнопку
            setGraphic(btn);
          } else {
            // иначе — пустая ячейка
            setGraphic(null);
          }
        }
      }
    });

    // 4) Загрузка данных
    refreshTable();

    // 5) Обработчики для стандартных кнопок
    addBookBtn.setOnAction(e -> openBookDialog());
    reserveBtn.setOnAction(e -> openReservationDialog());
  }

  private void refreshTable() {
    List<Book> all = bookService.getAllBooks();
    bookTable.getItems().setAll(all);
    countLabel.setText("Всего книг: " + all.size());
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
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void openReservationDialog() {
    Book sel = bookTable.getSelectionModel().getSelectedItem();
    if (sel == null) {
      new Alert(Alert.AlertType.WARNING,
          "Сначала выберите книгу.", ButtonType.OK).showAndWait();
      return;
    }
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reservation_dialog.fxml"));
      DialogPane pane = loader.load();
      ReservationController ctrl = loader.getController();
      ctrl.setBookId(sel.getId());
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
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void handleReturn(Long bookId) {
    // Найти ID брони для этой книги
    Long resId = reservationService.listAll().stream()
        .filter(r -> r.getBookId().equals(bookId))
        .map(Reservation::getId)
        .findFirst()
        .orElse(null);

    if (resId == null) {
      new Alert(Alert.AlertType.WARNING,
          "Книга не забронирована.", ButtonType.OK).showAndWait();
      return;
    }

    boolean success = reservationService.cancel(resId);
    if (success) {
      new Alert(Alert.AlertType.INFORMATION,
          "Бронирование отменено.", ButtonType.OK).showAndWait();
      refreshTable();
    }
  }

  // public void handleReturn(Long bookId) {
  // // найдем бронь для этой книги (берём первую или последнюю)
  // Long reservationId = reservationService.listAll().stream()
  // .filter(r -> r.getBookId().equals(bookId))
  // .map(Reservation::getId)
  // .findFirst()
  // .orElse(null);
  // if (reservationId == null) {
  // new Alert(Alert.AlertType.WARNING, "Эта книга не забронирована",
  // ButtonType.OK).showAndWait();
  // return;
  // }
  // boolean ok = reservationService.cancel(reservationId);
  // if (ok) {
  // new Alert(Alert.AlertType.INFORMATION, "Книга возвращена",
  // ButtonType.OK).showAndWait();
  // refreshTable();
  // }
  // }
}
