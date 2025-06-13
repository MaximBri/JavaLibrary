package com.example.library.controller;

import com.example.library.model.Newspaper;
import com.example.library.service.PublicationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class NewspaperController implements PublicationController {
  @FXML
  private TextField titleField;
  @FXML
  private TextField publisherField;
  @FXML
  private DatePicker publishDatePicker;
  @FXML
  private TextField editorNameField;
  @FXML
  private ComboBox<String> frequencyComboBox;
  @FXML
  private CheckBox nationalCheckBox;
  @FXML
  private TextField languageField;
  private final PublicationService publicationService;

  public NewspaperController() {
    this.publicationService = new PublicationService();
  }

  @FXML
  public void initialize() {
    publishDatePicker.setValue(LocalDate.now());
    frequencyComboBox.getItems().addAll("Ежедневная", "Еженедельная", "Ежемесячная", "Ежеквартальная");
    frequencyComboBox.getSelectionModel().selectFirst();
  }

  @Override
  public void save() {
    String title = titleField.getText().trim();
    String publisher = publisherField.getText().trim();
    LocalDate publishDate = publishDatePicker.getValue();
    String editorName = editorNameField.getText().trim();
    String frequency = frequencyComboBox.getValue();
    boolean isNational = nationalCheckBox.isSelected();
    String language = languageField.getText().trim();
    if (title.isEmpty() || editorName.isEmpty()) {
      showError("Поля Название и Редактор должны быть заполнены.");
      return;
    }
    Newspaper newspaper = new Newspaper();
    newspaper.setTitle(title);
    newspaper.setPublisher(publisher);
    newspaper.setPublishDate(publishDate);
    newspaper.setEditorName(editorName);
    newspaper.setFrequency(frequency);
    newspaper.setNational(isNational);
    newspaper.setLanguage(language);
    publicationService.addNewspaper(newspaper);
  }

  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Ошибка");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
