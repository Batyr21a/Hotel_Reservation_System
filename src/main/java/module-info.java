module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.logging;
    requires javafx.graphics;
    requires javafx.base;

    opens com.example to javafx.fxml;
    exports com.example;
}