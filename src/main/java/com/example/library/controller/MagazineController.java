package com.example.library.controller;

import com.example.library.model.Magazine;
import com.example.library.service.PublicationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class MagazineController implements PublicationController {
  @FXML
  private TextField titleField;
  @FXML
  private TextField publisherField;
  @FXML
  private DatePicker publishDatePicker;
  @FXML
  private TextField issnField;
  @FXML
  private TextField issueNumberField;
  @FXML
  private TextField categoryField;
  @FXML
  private CheckBox monthlyCheckBox;
  private final PublicationService publicationService;

  public MagazineController() {
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
      String issn = issnField.getText().trim();
      int issueNumber = Integer.parseInt(issueNumberField.getText().trim());
      String category = categoryField.getText().trim();
      boolean isMonthly = monthlyCheckBox.isSelected();
      if (title.isEmpty() || issn.isEmpty()) {
        showError("Поля Название и ISSN должны быть заполнены.");
        return;
      }
      Magazine magazine = new Magazine();
      magazine.setTitle(title);
      magazine.setPublisher(publisher);
      magazine.setPublishDate(publishDate);
      magazine.setIssn(issn);
      magazine.setIssueNumber(issueNumber);
      magazine.setCategory(category);
      magazine.setMonthly(isMonthly);
      publicationService.addMagazine(magazine);
    } catch (NumberFormatException e) {
      showError("Пожалуйста, введите корректный номер выпуска.");
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
