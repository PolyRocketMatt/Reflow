package com.github.polyrocketmatt.reflow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public interface Controller {

    void contextSwitch(String context);

    @FXML
    default void onClose(ActionEvent event) {
        System.exit(0);
    }

}
