package com.github.polyrocketmatt.restruct;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RestructUI extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestructUI.class.getResource("restruct.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1020);
        stage.setTitle("Restruct");
        //stage.getIcons()
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}