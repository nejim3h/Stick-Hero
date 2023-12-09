module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.swing;
    requires org.testng;
    requires org.junit.jupiter.api;
    requires junit;


    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}