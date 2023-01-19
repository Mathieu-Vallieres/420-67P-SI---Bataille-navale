package net.info420.bataillenavale;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MenuPrincipal extends Application {
    public static Stage mainWindow;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainWindow = primaryStage;
        primaryStage.setTitle("Battleship");
        primaryStage.setResizable(false);
        primaryStage.setMinHeight(350);
        primaryStage.setMaxWidth(280);

        StackPane root = new StackPane();
        primaryStage.setScene(new Scene(root, 350, 280));
        primaryStage.show();

        VBox menuBox = new VBox();

        Label title = new Label("Battleship");
        title.setFont(new Font("Arial", 32));

        Button jouer = new Button();
        jouer.setPrefSize(180, 50);
        jouer.setText("Jouer");

        CheckBox modeDifficile = new CheckBox();
        modeDifficile.setPrefSize(180, 50);
        modeDifficile.setText("Mode difficile");

        jouer.setOnAction(e -> {
            mainWindow.hide();
            new BatailleNavale(modeDifficile.isSelected());
        });

        Button quitter = new Button();
        quitter.setPrefSize(180, 50);
        quitter.setText("Quitter");

        quitter.setOnAction(e -> {
            System.exit(0);
        });

        menuBox.setSpacing(20);
        menuBox.getChildren().addAll(title, jouer, modeDifficile, quitter);
        menuBox.setAlignment(Pos.CENTER);
        root.getChildren().add(menuBox);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
