module com.gitub.polyrocketmatt.reflow.ui {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires batik.all;
    requires com.gitub.polyrocketmatt.reflow.decompiler;

    opens com.github.polyrocketmatt.reflow to javafx.fxml;
    exports com.github.polyrocketmatt.reflow;
    exports com.github.polyrocketmatt.reflow.context;
    opens com.github.polyrocketmatt.reflow.context to javafx.fxml;
}