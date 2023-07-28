package com.github.polyrocketmatt.reflow;

import com.github.polyrocketmatt.reflow.context.ApplicationContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class InitController implements Controller {

    @FXML Label initLabel;
    @FXML BorderPane initBorderPane;
    @FXML AnchorPane rootPane;

    public InitController() {}

    @SuppressWarnings("ConstantConditions")
    public void contextSwitch(String context) {
        //  Try to load decompiler view
        try {
            AnchorPane switchedContextPane = FXMLLoader.load(ReflowUI.class.getResource(context));
            Stage stage = ApplicationContext.CONTEXT.getStage();

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(switchedContextPane, width, height);

            //  Setting style
            scene.getStylesheets().add("stylesheet.css");

            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();

            //  Close the application
            System.exit(0);
        }
    }

    @Override
    public void onClose(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    public void onDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    public void onDragDropped(DragEvent event) {
        //  Get the file from the drag-board
        File file = event.getDragboard().getFiles().get(0);
        ApplicationContext.CONTEXT.addFile(file);

        contextSwitch(ApplicationContext.DECOMPILER);
    }

    @FXML
    public void onOpenFile(ActionEvent event) {
        //  Create file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select JAR/ZIP File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Jar Files", "*.jar"),
                new FileChooser.ExtensionFilter("Zip Files", "*.zip")
        );
        File selectedFile = fileChooser.showOpenDialog(ApplicationContext.CONTEXT.getStage());
        ApplicationContext.CONTEXT.addFile(selectedFile);

        contextSwitch(ApplicationContext.DECOMPILER);
    }

}
