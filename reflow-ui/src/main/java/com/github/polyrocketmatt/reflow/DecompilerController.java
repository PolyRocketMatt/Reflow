package com.github.polyrocketmatt.reflow;

import com.github.polyrocketmatt.reflow.context.ApplicationContext;
import com.github.polyrocketmatt.reflow.decompiler.ReflowJarHandler;
import com.github.polyrocketmatt.reflow.decompiler.struct.EntityStructure;
import com.github.polyrocketmatt.reflow.decompiler.utils.ByteUtils;
import com.github.polyrocketmatt.reflow.decompiler.wrapper.ClassWrapper;
import com.github.polyrocketmatt.reflow.decompiler.wrapper.EntityWrapper;
import com.github.polyrocketmatt.reflow.processing.SVGIconFactory;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DecompilerController implements Controller, Initializable {

    private File file;
    private ReflowJarHandler jarHandler;
    private EntityStructure<ClassWrapper> classWrapperStructure;

    @FXML AnchorPane rootPane;
    @FXML ProgressBar mainProgressBar;
    @FXML TreeView<String> classTreeView;

    public DecompilerController() {}

    @SuppressWarnings("ConstantConditions")
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupClassTreeView();
    }

    private void setupClassTreeView() {
        this.file = ApplicationContext.CONTEXT.getQueue().poll();
        if (this.file == null)
            throw new RuntimeException("No file was found in the queue when initializing the decompiler controller");
        this.jarHandler = new ReflowJarHandler();
        this.classTreeView.setVisible(false);
        this.loadJarFile();
        this.classTreeView.setVisible(true);
        this.classTreeView.setOnMouseClicked(this::decompile);
    }

    private List<ClassWrapper> handleInputStream(TreeItem<String> root, ZipInputStream zis) throws Exception{
        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<ClassWrapper> wrappers = new ArrayList<>();
        ZipEntry entry;

        while ((entry = zis.getNextEntry()) != null) {
            String name = entry.getName();
            byte[] data = ByteUtils.readBytes(zis);

            executor.execute(() -> {
                try {
                    EntityWrapper wrapper = this.jarHandler.parseClass(name, data);

                    if (wrapper instanceof ClassWrapper classWrapper)
                        wrappers.add(classWrapper);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
        executor.shutdown();

        if (!executor.awaitTermination(5, TimeUnit.MINUTES))
            throw new IllegalStateException("Failed to load JAR file");
        return wrappers;
    }

    private void constructTree(TreeItem<String> rootItem) {
        Task<Void> insertion = new Task<>() {
            @Override
            protected Void call() {
                List<String> parents = classWrapperStructure.getAbsoluteParents();

                //  Get the parents who have more than one child and exactly one child
                ArrayList<String> packageParents = new ArrayList<>(parents.stream().filter(parent -> classWrapperStructure.isPackage(parent)).toList());
                ArrayList<String> classParents = new ArrayList<>(parents.stream().filter(parent -> classWrapperStructure.isClass(parent)).toList());

                //  Sort both
                packageParents.sort(Comparator.comparingInt(o -> classWrapperStructure.getChildren(o).size()));
                classParents.sort(Comparator.comparingInt(o -> classWrapperStructure.getChildren(o).size()));

                //  Add package parents first, then class parents
                for (String parent : packageParents)
                    insertElement(parent.substring(0, parent.length() - 1), parent, rootItem, true);
                for (String parent : classParents)
                    insertElement(parent.substring(0, parent.length() - 1), parent, rootItem, false);

                return null;
            }
        };

        Thread insertionThread = new Thread(insertion);
        insertionThread.setDaemon(true);
        insertionThread.start();
    }

    private void insertElement(String element, String path, TreeItem<String> parent, boolean expandable) {
        boolean isPackage = classWrapperStructure.isPackage(path);
        ImageView itemIcon = SVGIconFactory.fromSVG((isPackage) ? "svg/package-item-plain.svg" : "svg/class-item-plain.svg");
        TreeItem<String> item = new TreeItem<>(element, itemIcon);
        Platform.runLater(() -> parent.getChildren().add(item));

        item.addEventHandler(TreeItem.branchExpandedEvent(), event -> expand(item, path));

        //  TODO: Work staged (i.e. when expanding, make sure the expanded level is already loaded)
        //  If the item is not expandable, we do not want to make it expandable currently
        if (expandable) {
            //  Expansion item
            TreeItem<String> expansionItem = new TreeItem<>("Loading...");

            //  Add expansion item to item
            Platform.runLater(() -> item.getChildren().add(expansionItem));
        }
    }

    private void expand(TreeItem<String> item, String path) {
        //  First check if the item was already loaded by checking if the only item is the expansion item
        if (item.getChildren().size() == 1 && item.getChildren().get(0).getValue().equals("Loading...")) {
            System.out.println("FFS");

            //  Remove the expansion item
            item.getChildren().remove(0);

            //  Get actual children
            List<String> children = classWrapperStructure.getChildren(path);
            List<String> packageChildren = new ArrayList<>(children.stream().filter(child -> classWrapperStructure.isPackage(path + child + "/")).toList());
            List<String> classChildren = new ArrayList<>(children.stream().filter(child -> !classWrapperStructure.isPackage(path + child + "/")).toList());

            //  Sort packages and classes alphabetically
            packageChildren.sort(String::compareToIgnoreCase);
            classChildren.sort(String::compareToIgnoreCase);

            //  Add package parents first, then class parents
            for (String subParent : packageChildren)
                insertElement(subParent, path + subParent + "/", item, true);
            for (String subChild : classChildren)
                //  TODO: In the future, this can be true if we want to add methods/constructors/inner class views
                insertElement(subChild, path + subChild + "/", item, false);
        }
    }

    private void expand(TreeItem<String> item) {
        StringBuilder path = new StringBuilder(item.getValue() + "/");
        TreeItem<String> parent = item.getParent();

        while (parent.getParent() != null) {
            path.insert(0, parent.getValue() + "/");
            parent = parent.getParent();
        }

        expand(item, path.toString());
    }

    private void loadJarFile() {
        try {
            ImageView rootIcon = SVGIconFactory.fromSVG("svg/jar-item-plain.svg");
            TreeItem<String> rootItem = new TreeItem<>(this.file.getName(), rootIcon);
            ZipInputStream zis = new ZipInputStream(new FileInputStream(this.file));
            List<ClassWrapper> classWrappers = handleInputStream(rootItem, zis);

            this.classWrapperStructure = new EntityStructure<>(classWrappers);
            this.classTreeView.setRoot(rootItem);
            this.constructTree(rootItem);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
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

        //  Update context
        File selectedFile = fileChooser.showOpenDialog(ApplicationContext.CONTEXT.getStage());
        ApplicationContext.CONTEXT.addFile(selectedFile);

        //  We do not want to switch context here, as we are already in the decompiler context
        try {
            setupClassTreeView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @FXML
    public void decompile(MouseEvent event) {
        try {
            TreeView<String> treeView = (TreeView<String>) event.getSource();
            TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
            String value = selectedItem.getValue();

            //  Check if the value is a class
            if (this.classWrapperStructure.isClass(value)) {
            } else {
                //  Expansion
                expand(selectedItem);
            }
        } catch (Exception ex) {
            //  Ignore
            System.out.println(event.getSource().getClass().getSimpleName());
        }
    }

}
