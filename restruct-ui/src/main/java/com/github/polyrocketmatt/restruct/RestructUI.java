package com.github.polyrocketmatt.restruct;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class RestructUI extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(RestructUI.class.getResource("restruct.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);

        scene.getStylesheets().add("/stylesheet.css");
        stage.setTitle(" Restruct");
        stage.getIcons().add(new Image("/restruct-32.png", 32, 32, true, false));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}