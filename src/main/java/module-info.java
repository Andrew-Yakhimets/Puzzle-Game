module com.example.puzzlegame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens com.example.puzzlegame to javafx.fxml;
    exports com.example.puzzlegame;
}