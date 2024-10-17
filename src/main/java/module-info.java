module com.test.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.test.demo to javafx.fxml;
    exports com.test.demo;
}