package com.github.polyrocketmatt.reflow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class ReflowUI extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        //  Load FXML
        FXMLLoader loader = new FXMLLoader(ReflowUI.class.getResource("reflow-init.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);

        //  Setting style
        scene.getStylesheets().add("stylesheet.css");

        stage.setTitle(" Reflow");
        stage.getIcons().add(new Image("/reflow-32.png", 32, 32, true, false));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}