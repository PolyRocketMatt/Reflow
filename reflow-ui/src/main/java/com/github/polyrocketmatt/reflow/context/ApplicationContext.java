package com.github.polyrocketmatt.reflow.context;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ApplicationContext {

    public static final String INIT = "reflow-init.fxml";
    public static final String DECOMPILER = "reflow-decompiler.fxml";
    public static final String SYSTEM_SEPARATOR = System.getProperty("file.separator");
    public static final ApplicationContext CONTEXT = new ApplicationContext();

    private final List<File> fileStore;
    private Stage stage = null;
    private double widthPenalty = 0.0;
    private double heightPenalty = 0.0;

    private ApplicationContext() {
        this.fileStore = new ArrayList<>();
    }

    public void addFile(File file) {
        fileStore.add(file);
    }

    public List<File> getFileStore() {
        return fileStore;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public double getWidthPenalty() {
        return widthPenalty;
    }

    public void setWidthPenalty(double widthPenalty) {
        this.widthPenalty = widthPenalty;
    }

    public double getHeightPenalty() {
        return heightPenalty;
    }

    public void setHeightPenalty(double heightPenalty) {
        this.heightPenalty = heightPenalty;
    }
}
