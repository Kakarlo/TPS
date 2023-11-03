module com.example.tps {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens tps to javafx.fxml;
    exports tps;
}