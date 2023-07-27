package com.github.polyrocketmatt.restruct;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class RestructController implements Initializable {

    @FXML Label initLabel;
    @FXML BorderPane initBorderPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML public void onDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML public void onDragDropped(DragEvent event) {
        //  Get the file from the drag-board
        File file = event.getDragboard().getFiles().get(0);

        //  Border pane becomes invisible
        try {
            initBorderPane.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
