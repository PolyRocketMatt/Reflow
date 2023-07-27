package com.github.polyrocketmatt.restruct.gui;

import com.github.polyrocketmatt.restruct.utils.decompilation.JExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

import static com.github.polyrocketmatt.restruct.Restruct.CLASS_HANDLER;
import static com.github.polyrocketmatt.restruct.Restruct.TEMP_DIR;

public class FlowActions {

    private final FlowInterface flowInterface;
    private final Logger logger = LoggerFactory.getLogger("FlowActions");

    private File currentFile;

    public FlowActions(FlowInterface flowInterface) {
        this.flowInterface = flowInterface;
    }

    public File getCurrentFile() {
        return currentFile;
    }


    public void openFileAction(ActionEvent event) {
        //  Create file chooser dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select JAR File");

        //  Show dialog and process result
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            logger.info("Selected file: " + currentFile.getName());
        }
    }

    public void dragFileAction(File file) {
        currentFile = file;
    }

    public void exitAction(ActionEvent event) {
        System.exit(0);
    }

    public void initializeClassTree() {
        try {
            //  Create class handler
            CLASS_HANDLER.init(currentFile);

            //  Extract classes from JAR and store in temp directory
            JExtractor.extractClasses(currentFile, TEMP_DIR, CLASS_HANDLER.getClasses().keySet());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
