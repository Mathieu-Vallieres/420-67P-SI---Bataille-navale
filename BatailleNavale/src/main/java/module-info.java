module net.info420.bataillenavale {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens net.info420.bataillenavale to javafx.fxml;
    exports net.info420.bataillenavale;
}