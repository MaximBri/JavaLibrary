package com.example.library;

import java.nio.charset.StandardCharsets;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
    loader.setCharset(StandardCharsets.UTF_8);

    Parent root = loader.load();
    primaryStage.setTitle("Библиотечная система");
    primaryStage.setScene(new Scene(root, 900, 600));
    primaryStage.show();
  }

  public static void main(String[] args) {
    System.setProperty("file.encoding", "UTF-8");
    System.setProperty("sun.jnu.encoding", "UTF-8");
    launch(args);
  }
}