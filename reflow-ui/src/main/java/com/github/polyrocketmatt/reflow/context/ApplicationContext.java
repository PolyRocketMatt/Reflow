package com.github.polyrocketmatt.reflow.context;

import javafx.stage.Stage;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ApplicationContext {

    public static final String INIT = "reflow-init.fxml";
    public static final String DECOMPILER = "reflow-decompiler.fxml";
    public static final String SYSTEM_SEPARATOR = System.getProperty("file.separator");
    public static final ApplicationContext CONTEXT = new ApplicationContext();

    private final Queue<File> queue;
    private Stage stage = null;
    private double widthPenalty = 0.0;
    private double heightPenalty = 0.0;

    private ApplicationContext() {
        this.queue = new LinkedList<>();
    }

    public void addFile(File file) {
        queue.add(file);
    }

    public Queue<File> getQueue() {
        return queue;
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
