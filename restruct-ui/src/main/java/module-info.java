module com.github.polyrocketmatt.restruct {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens com.github.polyrocketmatt.restruct to javafx.fxml;
    exports com.github.polyrocketmatt.restruct;
}