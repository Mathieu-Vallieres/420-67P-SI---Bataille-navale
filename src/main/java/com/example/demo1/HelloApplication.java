package com.example.demo1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    public static int WindowHeight = 520;
    public static int WindowWidth = 920;
    public static Stage mainWindow;
    public Pane[][] playerClickPanes;
    public Pane[][] computerClickPanes;
    public GridPane playerGrid;
    public GridPane computerGrid;
    @Override
    public void start(Stage primaryStage) throws IOException {
        playerClickPanes = new Pane[10][10];
        computerClickPanes = new Pane[10][10];

        mainWindow = primaryStage;

        primaryStage.setTitle("Battleship interface");
        primaryStage.setMinHeight(WindowHeight);
        primaryStage.setMinWidth(WindowWidth);
        primaryStage.setResizable(false);

        StackPane root = new StackPane();

        MenuBar menuBar = new MenuBar();
        root.getChildren().add(menuBar);
        Menu optionMenu = new Menu("Options");
        menuBar.getMenus().add(optionMenu);

        MenuItem newGame = new MenuItem("Nouvelle partie");

        newGame.setOnAction(e -> {
            System.out.println("Nouvelle partie");
        });

        MenuItem quitter = new MenuItem("Quitter");

        quitter.setOnAction(e -> {
            System.exit(0);
        });

        optionMenu.getItems().addAll(newGame, quitter);

        StackPane.setAlignment(menuBar, Pos.TOP_CENTER);

        GridPane rootGrid = new GridPane();
        //rootGrid.setStyle("-fx-background-color: white; -fx-grid-lines-visible: true");

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(440);
        col1.setMaxWidth(440);
        col1.setHgrow(Priority.ALWAYS);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(440);
        col2.setMaxWidth(440);
        col2.setHgrow(Priority.ALWAYS);

        ColumnConstraints bufferColumn = new ColumnConstraints();
        bufferColumn.setMinWidth(20);
        bufferColumn.setMaxWidth(20);

        rootGrid.getColumnConstraints().addAll(col1, bufferColumn, col2);

        RowConstraints row1 = new RowConstraints();
        row1.setPrefHeight(40);

        RowConstraints row2 = new RowConstraints();
        row2.setPrefHeight(440);

        rootGrid.getRowConstraints().add(row1);

        root.getChildren().add(rootGrid);

        StackPane.setMargin(rootGrid, new Insets(30, 20, 0, 20));

        primaryStage.setScene(new Scene(root, WindowWidth, WindowHeight));
        primaryStage.show();

        StackPane playerStack = new StackPane();
        playerGrid = new GridPane();
        playerStack.getChildren().add(playerGrid);

        SetupGrid(false);

        StackPane computerStack = new StackPane();
        computerGrid = new GridPane();
        computerStack.getChildren().add(computerGrid);

        SetupGrid(true);

        Label playerLabel = new Label("Joueur");
        playerLabel.setFont(new Font("Arial", 26));
        Label computerLabel = new Label("Ordinateur");
        computerLabel.setFont(new Font("Arial", 26));

        rootGrid.add(playerLabel, 0, 0);
        rootGrid.add(playerGrid, 0, 1);

        GridPane.setHalignment(playerLabel, HPos.CENTER);

        GridPane.setHalignment(playerStack, HPos.CENTER);

        rootGrid.add(computerLabel, 2, 0);
        rootGrid.add(computerGrid, 2, 1);

        GridPane.setHalignment(computerLabel, HPos.CENTER);

        GridPane.setHalignment(computerStack, HPos.CENTER);
    }

    private Pane CreateClickPane(int x, int y, boolean isComputer) {
        Pane newPane = new Pane();
        newPane.setMinSize(40, 40);
        newPane.setStyle("-fx-background-color: white; -fx-border-color: black;");

        if(isComputer) {
            computerGrid.add(newPane, x + 1, y + 1);
        } else {
            playerGrid.add(newPane, x + 1, y + 1);
        }

        newPane.setOnMouseClicked(e -> {
            ClickOnCell(newPane, x, y, isComputer);
        });

        return newPane;
    }

    private void SetupGrid(boolean isComputer) {
        GridPane grid;

        if(isComputer) {
            grid = computerGrid;
        } else {
            grid = playerGrid;
        }

        Pane cornerPane = new StackPane();
        cornerPane.setMinSize(40, 40);
        cornerPane.setStyle("-fx-background-color: white; -fx-border-color: black;");
        grid.add(cornerPane, 0, 0);

        for(int i = 1; i <= 10; i++) {
            StackPane labelPane = new StackPane();
            labelPane.setMinSize(40, 40);
            labelPane.setStyle("-fx-background-color: white; -fx-border-color: black;");

            Label label = new Label(Integer.toString(i));

            labelPane.getChildren().add(label);
            StackPane.setAlignment(label, Pos.CENTER);

            grid.add(labelPane, 0, i);
        }

        for(char i = 'A'; i <= 'J'; i++) {
            StackPane labelPane = new StackPane();
            labelPane.setMinSize(40, 40);
            labelPane.setStyle("-fx-background-color: white; -fx-border-color: black;");

            Label label = new Label(Character.toString(i));

            labelPane.getChildren().add(label);
            StackPane.setAlignment(label, Pos.CENTER);

            grid.add(labelPane, i - 65 + 1, 0);
        }

        if(isComputer) {
            for(int i = 0; i < 10; i++) {
                for(int j = 0; j < 10; j++) {
                    computerClickPanes[i][j] = CreateClickPane(i, j, true);
                }
            }
        } else {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    playerClickPanes[i][j] = CreateClickPane(i, j, false);
                }
            }
        }
    }

    private void ClickOnCell(Pane pane, int x, int y, boolean isComputer) {
        System.out.println("X: " + x + " " + "Y: " + y + " Computer: " + isComputer);
    }

    public static void main(String[] args) {
        launch();
    }
}