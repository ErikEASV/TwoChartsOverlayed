module com.example.chartlinechart {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.chartlinechart to javafx.fxml;
    exports com.example.chartlinechart;
}