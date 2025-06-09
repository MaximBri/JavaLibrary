package com.example.library.ui;

import com.example.library.controller.MainController;
import com.example.library.model.Book;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class ReturnButtonCellFactory implements Callback<TableColumn<Book, Void>, TableCell<Book, Void>> {
  @Override
  public TableCell<Book, Void> call(TableColumn<Book, Void> col) {
    return new TableCell<>() {
      private final Button returnBtn = new Button("Вернуть");

      {
        returnBtn.setOnAction(e -> {
          Book book = getTableView().getItems().get(getIndex());
          // сам механизм: ищем брони по book.getId(), берём их ID, и вызываем cancel
          getReturnAction().accept(book.getId());
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null);
        } else {
          setGraphic(new HBox(returnBtn));
        }
      }

      private java.util.function.Consumer<Long> getReturnAction() {
        // храним ссылку на service, например через синглтон или через контроллер
        return MainController.getInstance()::handleReturn; 
      }
    };
  }
}
