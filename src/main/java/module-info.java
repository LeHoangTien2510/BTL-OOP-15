module com.example.library {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.slf4j;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires jdk.compiler;
    opens com.example.library to javafx.fxml;
    exports Objects;
    opens Objects to javafx.fxml;
    opens Controller to javafx.fxml;
}