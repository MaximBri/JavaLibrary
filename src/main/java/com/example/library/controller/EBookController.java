package com.example.library.controller;

import com.example.library.model.EBook;
import com.example.library.service.PublicationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class EBookController implements PublicationController {
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
  @FXML
  private ComboBox<String> formatComboBox;
  @FXML
  private TextField fileSizeField;
  @FXML
  private TextField downloadUrlField;
  @FXML
  private CheckBox drmProtectedCheckBox;
  private final PublicationService publicationService;

  public EBookController() {
    this.publicationService = new PublicationService();
  }

  @FXML
  public void initialize() {
    publishDatePicker.setValue(LocalDate.now());
    formatComboBox.getItems().addAll("PDF", "EPUB", "MOBI", "AZW", "FB2");
    formatComboBox.getSelectionModel().selectFirst();
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
      String format = formatComboBox.getValue();
      double fileSize = Double.parseDouble(fileSizeField.getText().trim());
      String downloadUrl = downloadUrlField.getText().trim();
      boolean isDrmProtected = drmProtectedCheckBox.isSelected();
      if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
        showError("Поля Название, Автор и ISBN должны быть заполнены.");
        return;
      }
      EBook ebook = new EBook();
      ebook.setTitle(title);
      ebook.setPublisher(publisher);
      ebook.setPublishDate(publishDate);
      ebook.setAuthor(author);
      ebook.setIsbn(isbn);
      ebook.setPageCount(pageCount);
      ebook.setGenre(genre);
      ebook.setFormat(format);
      ebook.setFileSizeMB(fileSize);
      ebook.setDownloadUrl(downloadUrl);
      ebook.setDrmProtected(isDrmProtected);
      publicationService.addEBook(ebook);
    } catch (NumberFormatException e) {
      showError("Пожалуйста, введите корректные числовые значения.");
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
