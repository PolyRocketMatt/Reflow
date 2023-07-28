package com.github.polyrocketmatt.reflow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.File;

public class ReflowInitController implements Controller {

    @FXML Label initLabel;
    @FXML BorderPane initBorderPane;
    @FXML AnchorPane rootPane;
    @FXML AnchorPane filePane;

    public ReflowInitController() {}

    @SuppressWarnings("ConstantConditions")
    public void contextSwitch(String context) {
        //  Try to load decompiler view
        try {
            AnchorPane switchedContextPane = FXMLLoader.load(ReflowUI.class.getResource(context));
            rootPane.getChildren().setAll(switchedContextPane);
        } catch (Exception ex) {
            //  Close the application
            System.exit(0);
        }
    }

    @FXML
    public void onClose(ActionEvent event) {
        //  TODO: Delete TMP files

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

        contextSwitch(ApplicationContext.DECOMPILER);
    }

}
