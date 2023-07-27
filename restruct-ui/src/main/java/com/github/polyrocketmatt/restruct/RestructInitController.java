package com.github.polyrocketmatt.restruct;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.File;
import java.util.Timer;

public class RestructInitController implements Controller {

    @FXML Label initLabel;
    @FXML BorderPane initBorderPane;
    @FXML AnchorPane rootPane;
    @FXML AnchorPane filePane;

    public RestructInitController() {}

    @SuppressWarnings("ConstantConditions")
    public void contextSwitch(String context) {
        //  Try to load decompiler view
        try {
            AnchorPane switchedContextPane = FXMLLoader.load(RestructUI.class.getResource(context));
            rootPane.getChildren().setAll(switchedContextPane);
        } catch (Exception ex) {
            //  Close the application
            System.exit(0);
        }
    }

    @FXML void fileMenuClickHandler(MouseEvent event) {
        filePane.setVisible(true);
    }

    @FXML void borderPaneClickHandler(MouseEvent event) {
        initBorderPane.setVisible(false);
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
