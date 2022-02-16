module com.example.lesson_3_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.lesson_3_2 to javafx.fxml;
    exports com.example.lesson_3_2;
}