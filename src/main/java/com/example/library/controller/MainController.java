package com.example.library.controller;

import com.example.library.dao.BookDao;
import com.example.library.dao.EBookDao;
import com.example.library.dao.MagazineDao;
import com.example.library.dao.NewspaperDao;
import com.example.library.model.*;
import com.example.library.model.user.User;
import com.example.library.service.AuthService;
import com.example.library.service.PublicationService;
import com.example.library.service.ReservationService;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainController {
  @FXML
  private TextField loginField;
  @FXML
  private PasswordField passwordField;
  @FXML
  private Button loginBtn;
  @FXML
  private TableView<Publication> publicationTable;
  @FXML
  private TableColumn<Publication, Long> colId;
  @FXML
  private TableColumn<Publication, String> colTitle;
  @FXML
  private TableColumn<Publication, String> colType;
  @FXML
  private TableColumn<Publication, String> colDetails;
  @FXML
  private TableColumn<Publication, String> colStatus;
  @FXML
  private TableColumn<Publication, Void> colAction;
  @FXML
  private Button addBookBtn;
  @FXML
  private Button addMagazineBtn;
  @FXML
  private Button addNewspaperBtn;
  @FXML
  private Button addEBookBtn;
  @FXML
  private Button reserveBtn;
  @FXML
  private Button deleteBtn;
  @FXML
  private Label countLabel;
  @FXML
  private ComboBox<String> typeFilter;
  @FXML
  private TextField searchField;
  @FXML
  private Button searchBtn;

  private final PublicationService publicationService;
  private final ReservationService reservationService;

  private User currentUser;
  private AuthService authService;

  public MainController() {
    this.publicationService = new PublicationService();
    this.reservationService = new ReservationService();
  }

  @FXML
  public void initialize() {
    authService = new AuthService();

    applyPermissions();
    setupTypeFilter();
    bindColumnWidths();
    setupColumns();
    setupRowHighlighting();
    setupActionColumn();
    setupButtons();
    setupSearch();
    publicationService.cancelExpiredReservations();
    loadPublications();
  }

  private void applyPermissions() {
    boolean loggedIn = currentUser != null;

    boolean canAdd = loggedIn && currentUser.canAddPublication();
    addBookBtn.setVisible(canAdd);
    addBookBtn.setManaged(canAdd);
    addMagazineBtn.setVisible(canAdd);
    addMagazineBtn.setManaged(canAdd);
    addNewspaperBtn.setVisible(canAdd);
    addNewspaperBtn.setManaged(canAdd);
    addEBookBtn.setVisible(canAdd);
    addEBookBtn.setManaged(canAdd);

    boolean canDelete = loggedIn && currentUser.canDeletePublication();
    deleteBtn.setVisible(canDelete);
    deleteBtn.setManaged(canDelete);

    boolean canReserve = loggedIn && currentUser.canReservePublication();
    reserveBtn.setVisible(canReserve);
    reserveBtn.setManaged(canReserve);
  }

  public void setCurrentUser(User user) {
    this.currentUser = user;
  }

  @FXML
  private void handleLogin() {
    String login = loginField.getText().trim();
    String password = passwordField.getText().trim();
    if (login.isEmpty() || password.isEmpty()) {
      showAlert("Введите логин и пароль.");
      return;
    }
    currentUser = authService.login(login, password);
    showAlert("Успешный вход: " + currentUser.getName() + ", роль: " + currentUser.getRole());
    applyPermissions();
    publicationTable.refresh();
  }

  private void setupTypeFilter() {
    typeFilter.setItems(FXCollections.observableArrayList(
        "Все публикации", "Книги", "Журналы", "Газеты", "Электронные книги"));
    typeFilter.getSelectionModel().selectFirst();
    typeFilter.setOnAction(e -> loadPublications());
  }

  private void setupSearch() {
    searchBtn.setOnAction(e -> {
      String query = searchField.getText().trim();
      if (query.isEmpty()) {
        loadPublications();
      } else {
        List<Publication> results = publicationService.findPublicationsByTitle(query);
        updatePublicationTable(results);
      }
    });
  }

  private void bindColumnWidths() {
    DoubleBinding totalWidth = publicationTable.widthProperty().subtract(2);
    colId.prefWidthProperty().bind(totalWidth.multiply(0.05));
    colTitle.prefWidthProperty().bind(totalWidth.multiply(0.25));
    colType.prefWidthProperty().bind(totalWidth.multiply(0.10));
    colDetails.prefWidthProperty().bind(totalWidth.multiply(0.30));
    colStatus.prefWidthProperty().bind(totalWidth.multiply(0.20));
    colAction.prefWidthProperty().bind(totalWidth.multiply(0.10));
  }

  private void setupColumns() {
    colId.setCellValueFactory(new PropertyValueFactory<>("id"));
    colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));

    colType.setCellValueFactory(cell -> {
      Publication publication = cell.getValue();
      String type = "";
      if (publication instanceof Book && !(publication instanceof EBook)) {
        type = "Книга";
      } else if (publication instanceof Magazine) {
        type = "Журнал";
      } else if (publication instanceof Newspaper) {
        type = "Газета";
      } else if (publication instanceof EBook) {
        type = "Эл. книга";
      }
      return new ReadOnlyStringWrapper(type);
    });

    colDetails.setCellValueFactory(cell -> new ReadOnlyStringWrapper(getDetails(cell.getValue())));

    colStatus.setCellValueFactory(cell -> {
      Publication pub = cell.getValue();
      if (!pub.isReserved()) {
        return new ReadOnlyStringWrapper("В наличии");
      }
      List<Reservation> reservations = reservationService.getReservationsForBook(pub.getId());
      Optional<Reservation> last = reservations.stream()
          .max(Comparator.comparing(Reservation::getDueDate));
      return last.map(r -> new ReadOnlyStringWrapper(
          String.format("Забронирована: %s до %s", r.getCustomerName(), r.getDueDate())))
          .orElseGet(() -> new ReadOnlyStringWrapper("Забронирована"));
    });
  }

  private String getDetails(Publication publication) {
    if (publication instanceof Book && !(publication instanceof EBook)) {
      Book book = (Book) publication;
      return String.format("Автор: %s, ISBN: %s", book.getAuthor(), book.getIsbn());
    } else if (publication instanceof Magazine) {
      Magazine magazine = (Magazine) publication;
      return String.format("Выпуск: %d, ISSN: %s", magazine.getIssueNumber(), magazine.getIssn());
    } else if (publication instanceof Newspaper) {
      Newspaper newspaper = (Newspaper) publication;
      return String.format("Редактор: %s, Периодичность: %s", newspaper.getEditorName(), newspaper.getFrequency());
    } else if (publication instanceof EBook) {
      EBook ebook = (EBook) publication;
      return String.format("Автор: %s, Формат: %s, Размер: %.2f МБ",
          ebook.getAuthor(), ebook.getFormat(), ebook.getFileSizeMB());
    }
    return "";
  }

  private void setupRowHighlighting() {
    publicationTable.setRowFactory(table -> new TableRow<Publication>() {
      @Override
      protected void updateItem(Publication publication, boolean empty) {
        super.updateItem(publication, empty);
        if (empty || publication == null) {
          setStyle("");
        } else if (publication.isReserved()) {
          setStyle("-fx-background-color: #ffdddd;");
        } else {
          if (publication instanceof Book && !(publication instanceof EBook)) {
            setStyle("-fx-background-color: #e6f2ff;"); // Голубой для книг
          } else if (publication instanceof Magazine) {
            setStyle("-fx-background-color: #e6ffe6;"); // Зеленый для журналов
          } else if (publication instanceof Newspaper) {
            setStyle("-fx-background-color: #fff2e6;"); // Оранжевый для газет
          } else if (publication instanceof EBook) {
            setStyle("-fx-background-color: #f9e6ff;"); // Фиолетовый для эл. книг
          }
        }
      }
    });
  }

  private void setupActionColumn() {
    colAction.setCellFactory(col -> new TableCell<Publication, Void>() {
      private final Button returnBtn = new Button("Вернуть");

      {
        returnBtn.getStyleClass().add("toolbar-button");
        returnBtn.setOnAction(e -> {
          Publication pub = getTableRow().getItem();
          if (pub != null) {
            handleReturn(pub);
          }
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(null);
        if (empty)
          return;

        Publication pub = getTableRow().getItem();
        if (pub == null)
          return;

        Optional<String> reservedByOpt = reservationService.getReservedBy(pub.getId());
        boolean isReserved = reservedByOpt.isPresent();
        boolean isLoggedIn = currentUser != null;
        boolean isLibrarian = isLoggedIn && currentUser.canAddPublication();
        boolean isOwner = isLoggedIn && reservedByOpt.map(currentUser.getLogin()::equals).orElse(false);

        if (isReserved && (isLibrarian || isOwner)) {
          setGraphic(returnBtn);
        }
      }
    });
  }

  private void setupButtons() {
    addBookBtn.setOnAction(e -> openAddDialog("book_dialog.fxml", "Добавить книгу"));
    addMagazineBtn.setOnAction(e -> openAddDialog("magazine_dialog.fxml", "Добавить журнал"));
    addNewspaperBtn.setOnAction(e -> openAddDialog("newspaper_dialog.fxml", "Добавить газету"));
    addEBookBtn.setOnAction(e -> openAddDialog("ebook_dialog.fxml", "Добавить электронную книгу"));
    reserveBtn.setOnAction(e -> openReservationDialog());
    deleteBtn.setOnAction(e -> handleDelete());
  }

  private void loadPublications() {
    String filter = typeFilter.getSelectionModel().getSelectedItem();
    List<Publication> publications;

    switch (filter) {
      case "Книги":
        publications = new ArrayList<>(publicationService.getAllBooks().stream()
            .filter(b -> !(b instanceof EBook))
            .collect(Collectors.toList()));
        break;
      case "Журналы":
        publications = new ArrayList<>(publicationService.getAllMagazines());
        break;
      case "Газеты":
        publications = new ArrayList<>(publicationService.getAllNewspapers());
        break;
      case "Электронные книги":
        publications = new ArrayList<>(publicationService.getAllEBooks());
        break;
      default:
        publications = publicationService.getAllPublications();
    }

    for (Publication pub : publications) {
      boolean hasActive = reservationService
          .getReservationsForBook(pub.getId()).stream()
          .anyMatch(r -> !r.getDueDate().isBefore(LocalDate.now()));
      pub.setReserved(hasActive);
    }

    updatePublicationTable(publications);
  }

  private void updatePublicationTable(List<Publication> publications) {
    publicationTable.getItems().clear();
    publicationTable.getItems().addAll(publications);
    countLabel.setText("Всего публикаций: " + publications.size());
  }

  private void openAddDialog(String fxmlFile, String title) {
    if (!currentUser.canReservePublication())
      return;
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
      loader.setCharset(StandardCharsets.UTF_8);

      DialogPane pane = loader.load();
      Dialog<ButtonType> dialog = new Dialog<>();
      dialog.setDialogPane(pane);
      dialog.setTitle(title);

      Object controller = loader.getController();
      dialog.setResultConverter(bt -> {
        if (bt != null && bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
          if (controller instanceof PublicationController) {
            ((PublicationController) controller).save();
          }
        }
        return null;
      });

      dialog.showAndWait();
      refreshTable();
    } catch (IOException ex) {
      showError("Не удалось открыть окно добавления", ex);
    }
  }

  private void openReservationDialog() {
    Publication publication = publicationTable.getSelectionModel().getSelectedItem();
    if (publication == null) {
      showWarning("Сначала выберите публикацию для бронирования.");
      return;
    }
    if (publication.isReserved()) {
      showWarning("Выбранная публикация уже забронирована.");
      return;
    }

    TextInputDialog daysDialog = new TextInputDialog("7");
    daysDialog.setTitle("Бронирование");
    daysDialog.setHeaderText(
        String.format("Бронируется: «%s» для %s", publication.getTitle(), currentUser.getLogin()));
    daysDialog.setContentText("Введите количество дней:");

    Optional<String> daysResult = daysDialog.showAndWait();
    if (daysResult.isPresent()) {
      try {
        int days = Integer.parseInt(daysResult.get().trim());
        if (days <= 0) {
          showWarning("Количество дней должно быть положительным числом.");
          return;
        }

        LocalDate dueDate = LocalDate.now().plusDays(days);
        boolean success = publicationService.reservePublication(
            publication.getId(),
            currentUser.getLogin(),
            dueDate);

        if (success) {
          refreshTable();
          showInfo(String.format(
              "Публикация «%s» успешно забронирована на %s до %s.",
              publication.getTitle(),
              currentUser.getLogin(),
              dueDate));
        } else {
          showError("Не удалось забронировать публикацию.");
        }
      } catch (NumberFormatException e) {
        showWarning("Пожалуйста, введите корректное число дней.");
      }
    }
  }

  private void handleReturn(Publication publication) {
    Long pubId = publication.getId();

    Optional<Reservation> activeRes = reservationService.getReservationsForBook(pubId).stream()
        .filter(r -> !r.getDueDate().isBefore(LocalDate.now()))
        .findFirst();

    if (activeRes.isEmpty()) {
      showWarning("Эта публикация не забронирована.");
      return;
    }

    Reservation reservation = activeRes.get();
    String reservedBy = reservation.getCustomerName();

    boolean isLibrarian = currentUser.canAddPublication();
    boolean isOwner = currentUser.getLogin().equals(reservedBy);
    if (!isLibrarian && !isOwner) {
      showWarning("Вы не можете вернуть чужую бронь.");
      return;
    }

    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
        String.format("Вернуть '%s', зарезервированную %s?", publication.getTitle(), reservedBy),
        ButtonType.YES, ButtonType.NO);
    if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
      return;
    }

    boolean success = reservationService.cancelReservation(reservation.getId());
    if (success) {
      refreshTable();
      showInfo("Публикация успешно возвращена.");
    } else {
      showError("Не удалось вернуть публикацию.");
    }
  }

  private void handleDelete() {
    if (!currentUser.canReservePublication())
      return;
    Publication publication = publicationTable.getSelectionModel().getSelectedItem();
    if (publication == null) {
      showWarning("Сначала выберите публикацию для удаления.");
      return;
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Подтверждение удаления");
    alert.setHeaderText("Удаление публикации");
    alert.setContentText(String.format("Вы уверены, что хотите удалить публикацию '%s'?", publication.getTitle()));

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
      publicationService.deletePublication(publication);
      refreshTable();
    }
  }

  private void showAlert(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Авторизация");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  private void refreshTable() {
    loadPublications();
  }

  private void showInfo(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Информация");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  private void showWarning(String message) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("Предупреждение");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Ошибка");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  private void showError(String message, Exception ex) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Ошибка");
    alert.setHeaderText(message);
    alert.setContentText(ex.getMessage());

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    ex.printStackTrace(pw);

    TextArea textArea = new TextArea(sw.toString());
    textArea.setEditable(false);
    textArea.setWrapText(true);
    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setMaxHeight(Double.MAX_VALUE);

    alert.getDialogPane().setExpandableContent(new VBox(textArea));
    alert.showAndWait();
  }
}
