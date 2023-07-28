package com.github.polyrocketmatt.reflow;

import com.github.polyrocketmatt.reflow.context.ApplicationContext;
import com.github.polyrocketmatt.reflow.decompiler.ReflowJarHandler;
import com.github.polyrocketmatt.reflow.decompiler.wrapper.ClassWrapper;
import com.github.polyrocketmatt.reflow.decompiler.wrapper.EntityWrapper;
import com.github.polyrocketmatt.reflow.palette.ReflowColor;
import com.github.polyrocketmatt.reflow.palette.ReflowPalette;
import com.github.polyrocketmatt.reflow.processing.BufferedImageTranscoder;
import com.github.polyrocketmatt.reflow.processing.SVGIconFactory;
import com.github.polyrocketmatt.reflow.processing.Scalr;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.Buffer;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DecompilerController implements Controller, Initializable {

    private final File file;
    private final ReflowJarHandler jarHandler;

    private final int totalClassCount;

    @FXML AnchorPane rootPane;
    @FXML ProgressBar mainProgressBar;
    @FXML TreeView<String> classTreeView;

    public DecompilerController() {
        this.file = ApplicationContext.CONTEXT.getFileStore().get(0);
        this.jarHandler = ReflowJarHandler.of(file);

        System.out.println("File: " + file.getAbsolutePath());

        //  Count the number of classes in the JAR
        int classCount = 0;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null)
                if (entry.getName().endsWith(".class"))
                    classCount++;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        System.out.println("Class count: " + classCount);
        this.totalClassCount = classCount;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ImageView rootIcon = SVGIconFactory.fromSVG("svg/jar-item-plain.svg");
        TreeItem<String> rootItem = new TreeItem<>(this.file.getName(), rootIcon);

        loadJarFile(rootItem);
        loadClassTree(rootItem);
    }

    private void loadJarFile(TreeItem<String> rootItem) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            double progress = 0;
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    EntityWrapper wrapper = jarHandler.parseClass(zis, entry);
                    if (wrapper instanceof ClassWrapper classWrapper) {
                        insertIntoTree(rootItem, classWrapper);

                        try {
                            double t = progress++ / totalClassCount;
                            mainProgressBar.setProgress(t);
                            ReflowColor color = ReflowColor.lerp(ReflowPalette.BLUE_ACCENT, ReflowPalette.GREEN_ACCENT, t);
                            mainProgressBar.setStyle("-fx-accent: #" + color.hex());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            mainProgressBar.setProgress(0.0);
        }, 100, TimeUnit.MILLISECONDS);

        scheduler.shutdown();
    }

    private void loadClassTree(TreeItem<String> rootItem) {
        classTreeView.setRoot(rootItem);
    }

    private void insertIntoTree(TreeItem<String> root, ClassWrapper wrapper) {
        String[] paths = wrapper.getName().split("/");
        TreeItem<String> current = root;

        for (int i = 0; i < paths.length; i++) {
            boolean isLast = i == paths.length - 1;

            // See if the current node has a child with the same name
            String subPath = paths[i];
            TreeItem<String> child = current.getChildren().stream()
                    .filter(treeItem -> treeItem.getValue().equals(subPath))
                    .findFirst()
                    .orElse(null);

            // If a child was found, we can continue down the tree
            if (child != null) {
                current = child;
                continue;
            }

            // If no child was found, we need to create a new node
            ImageView classIcon = SVGIconFactory.fromSVG("svg/class-item.svg");
            TreeItem<String> newNode = (isLast) ? new TreeItem<>(subPath, classIcon) : new TreeItem<>(subPath);

            // Add the new node to the current node
            current.getChildren().add(newNode);

            // Set the new node as the current node
            current = newNode;
        }
    }

    @Override
    public void onClose(ActionEvent event) {
        System.exit(0);
    }

    @SuppressWarnings("ConstantConditions")
    public void contextSwitch(String context) {
        //  Try to load decompiler view
        try {
            AnchorPane switchedContextPane = FXMLLoader.load(ReflowUI.class.getResource(context));
            Stage stage = ApplicationContext.CONTEXT.getStage();

            double width = stage.getWidth() - ApplicationContext.CONTEXT.getWidthPenalty();
            double height = stage.getHeight() - ApplicationContext.CONTEXT.getHeightPenalty();

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

    @FXML
    public void onOpenFile(ActionEvent event) {
        //  Create file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Jar Files", "*.jar"),
                new FileChooser.ExtensionFilter("Zip Files", "*.zip")
        );
        File selectedFile = fileChooser.showOpenDialog(ApplicationContext.CONTEXT.getStage());
        ApplicationContext.CONTEXT.addFile(selectedFile);
        contextSwitch(ApplicationContext.DECOMPILER);
    }

}
