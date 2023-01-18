package net.info420.bataillenavale;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class BatailleNavale extends Application {
    public static int WindowHeight = 560;
    public static int WindowWidth = 920;
    public static Stage mainWindow;
    public Pane[][] playerClickPanes;
    public Pane[][] computerClickPanes;
    public GridPane playerGrid;
    public GridPane computerGrid;
    public Image BoatHeadIMG;
    public Image BoatBodyIMG;
    public Image BoatTailIMG;
    public Label PlacementLabel;

    public ArrayList<ImageView> previewImages;
    public ArrayList<ImageView> boatImages;

    public static Direction boatDirection;
    public static int boatSize = 3;
    public static Vector2 cursorPos;

    /**Grille contenant les informations sur les bateaux de l'ordinateur*/
    public static int [][] grilleOrdi = new int [10][10];

    /**Grille contenant les informations sur les bateaux du joueur*/
    public static int [][] grilleJoueur = new int [10][10];

    public static int placementBateau = 1;

    public static ColorAdjust previewEffect;
    public static ColorAdjust normalEffect;
    public static ColorAdjust hitEffect;

    /**
     * HashMap permettant de lier l'ID du bateau à sa grosseur (nombre de case)
     */
    @SuppressWarnings("serial")
    public static HashMap<Integer, Integer> grandeurBateaux = new HashMap<Integer, Integer>() {{
        put(1, 5);
        put(2, 4);
        put(3, 3);
        put(4, 3);
        put(5, 2);
    }};

    /**
     * HashMap permettant de lier le nom d'un bateau à son ID
     */
    @SuppressWarnings("serial")
    public static HashMap<String, Integer> idBateaux = new HashMap<String, Integer>() {{
        put("Porte-Avions", 1);
        put("Croiseur", 2);
        put("Contre-Torpilleur", 3);
        put("Sous-Marin", 4);
        put("Torpilleur", 5);
    }};

    public static double map(double value, double start, double stop, double targetStart, double targetStop) {
        return targetStart + (targetStop - targetStart) * ((value - start) / (stop - start));
    }


    @Override
    public void start(Stage primaryStage) throws IOException {
        previewEffect = new ColorAdjust();
        previewEffect.setBrightness(0.5);

        normalEffect = new ColorAdjust();

        hitEffect = new ColorAdjust();
        hitEffect.setHue(map(187, 0, 360, -1, 1));
        hitEffect.setSaturation(1);
        hitEffect.setBrightness(0.2);

        BoatHeadIMG = new Image(new FileInputStream(System.getProperty("user.dir") + "/BatailleNavale/src/images/head.png"));
        BoatBodyIMG = new Image(new FileInputStream(System.getProperty("user.dir") + "/BatailleNavale/src/images/body.png"));
        BoatTailIMG = new Image(new FileInputStream(System.getProperty("user.dir") + "/BatailleNavale/src/images/tail.png"));

        previewImages = new ArrayList<ImageView>();
        boatImages = new ArrayList<ImageView>();

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

        RowConstraints row3 = new RowConstraints();
        row3.setPrefHeight(40);

        rootGrid.getRowConstraints().addAll(row1, row2, row3);

        root.getChildren().add(rootGrid);

        StackPane.setMargin(rootGrid, new Insets(30, 20, 0, 20));

        Scene primaryScene = new Scene(root, WindowWidth, WindowHeight);

        primaryStage.setScene(primaryScene);
        primaryStage.show();

        primaryScene.setOnMousePressed(e -> {
            if(e.getButton() == MouseButton.SECONDARY) {
                ToggleVerticalHorizontal();
            }
        });

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

        PlacementLabel = new Label("Placement de: " + getBoatName() + " (" + grandeurBateaux.get(placementBateau) + " cases)");

        rootGrid.add(PlacementLabel, 0, 2);
    }

    public static String getBoatName() {
        for(Map.Entry<String, Integer> boat : idBateaux.entrySet()) {
            if(boat.getValue() == placementBateau) {
                return boat.getKey();
            }
        }

        return "";
    }

    public static void remplirGrillesDeZeros()
    {
        for(int i = 0; i < 10; i ++)
        {
            for(int j = 0; j < 10; j ++)
            {
                grilleOrdi[i][j] = 0;
                grilleJoueur[i][j] = 0;
            }
        }
    }

    public static boolean PosOk(int[][] grille, int ligne, int colonne, Direction direction, int tailleBateau) {
        if(direction == Direction.Horizontal) { // Si la direction est horizontal
            // Si la colonne additionné de la taille du bateau est inférieur ou égal à 10
            if(colonne + tailleBateau <= 10) {
                // On boucle à l'endroit où le bateau devrait être placé
                for(int i = colonne; i < colonne + tailleBateau; i++) {
                    // Si il n'y a pas de l'eau à l'endroit où on veut le placer, on retourne false
                    if(grille[ligne][i] != 0) {
                        return false;
                    }
                }
                // Si on se rend ici, il y a uniquement de l'eau où on veut placer le bateau, on retourne donc true
                return true;
            } else { // On retourne false si la ligne additionné de la taille du bateau est supérieur ou égal à 9
                return false;
            }
        } else if(direction == Direction.Vertical) { // Si la direction est verticale
            // Si la ligne additionné de la taille du bateau est inférieur ou égal à 10
            if(ligne + tailleBateau <= 10) {
                // On boucle à l'endroit où le bateau devrait être placé
                for(int i = ligne; i < ligne + tailleBateau; i++) {
                    // Si il n'y a pas de l'eau à l'endroit où on veut le placer, on retourne false
                    if(grille[i][colonne] != 0) {
                        return false;
                    }
                }
                // Si on se rend ici, il y a uniquement de l'eau où on veut placer le bateau, on retourne donc true
                return true;
            } else { // On retourne false si la ligne additionné de la taille du bateau est supérieur ou égal à 9
                return false;
            }
        }

        // Si la direction est ni horizontal, ni vertical, on retourne false
        return false;
    }

    /**
     * Fonction permettant de placer un bateau dans une grille
     * @param grille La grille où on veut placer le bateau
     * @param ligne La ligne où on veut placer le bateau
     * @param colonne La colonne où on veut placer le bateau
     * @param direction La direction où on veut placer le bateau
     * @param idBateau L'ID du bateau qu'on veut placer
     */
    public static void placerBateau(int[][] grille, int ligne, int colonne, Direction direction, int idBateau) {
        if(direction == Direction.Horizontal) { // Si la direction est horizontal
            // On boucle à partir de la colonne jusqu'à la colonne additionné de la grandeur du bateau
            for(int i = colonne; i < colonne + grandeurBateaux.get(idBateau); i++) {
                // On met l'ID du bateau sur les bonnes cases
                grille[ligne][i] = idBateau;
            }
        } else if(direction == Direction.Vertical) { // Si la direction est verticale
            // On boucle à partir de la ligne jusqu'à la ligne additionné de la grandeur du bateau
            for(int i = ligne; i < ligne + grandeurBateaux.get(idBateau); i++) {
                // On met l'ID du bateau sur les bonnes cases
                grille[i][colonne] = idBateau;
            }
        }
    }

    /**
     * Fonction qui vérifie si tous les bateaux de la grille ont été coulés
     * @param grille Grille qu'on veut vérifier si tous les bateaux ont été coulés
     * @return Retourne si tous les bateaux de la grille ont été coulés
     */
    public static boolean aPerdu(int[][] grille) {
        // On boucle à travers les lignes
        for(int i = 0; i < 10; i++) {
            // On boucle à travers les colonnes
            for(int j = 0; j < 10; j++)  {
                // Si la case n'est pas de l'eau (0) et la case n'est pas un bateau touché (6), les bateaux ne sont pas toutes coulés
                if(grille[i][j] != 0 && grille[i][j] != 6) {
                    return false;
                }
            }
        }

        // Toutes les cases sont de l'eau (0) et des bateaux touchées (6), la grille a perdu, on retourne false
        return true;
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

        if(!isComputer) {
            newPane.setOnMouseEntered(e -> {
                EnterCell(newPane, x, y);
            });
        }

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

    private void EnterCell(Pane pane, int x, int y) {
        for(ImageView image : previewImages) {
            if(image.getParent() != null) {
                ((Pane) image.getParent()).getChildren().remove(image);
            }
        }

        cursorPos = new Vector2(x, y);

        previewImages.clear();

        int rotation = 0;
        if(boatDirection == Direction.Horizontal) {
            rotation = 90;
        }

        ImageView head = new ImageView(BoatHeadIMG);
        head.setEffect(previewEffect);
        head.setRotate(rotation);
        ImageView body = new ImageView(BoatBodyIMG);
        body.setEffect(normalEffect);
        body.setRotate(rotation);
        ImageView tail = new ImageView(BoatTailIMG);
        tail.setEffect(hitEffect);
        tail.setRotate(rotation);

        if(boatDirection == Direction.Horizontal) {
            if(x + boatSize <= 10) {
                try {
                    playerClickPanes[x][y].getChildren().add(tail);
                    playerClickPanes[x + 1][y].getChildren().add(body);
                    playerClickPanes[x + 2][y].getChildren().add(head);
                } catch (Exception ex) {
                    System.out.println("Position invalide");
                }
            }
        } else {
            if(y + boatSize <= 10) {
                try {
                    playerClickPanes[x][y].getChildren().add(head);
                    playerClickPanes[x][y + 1].getChildren().add(body);
                    playerClickPanes[x][y + 2].getChildren().add(tail);
                } catch (Exception ex) {
                    System.out.println("Position invalide");
                }
            }
        }

        previewImages.add(head);
        previewImages.add(body);
        previewImages.add(tail);
    }

    private void ToggleVerticalHorizontal() {
        if(boatDirection == Direction.Horizontal) {
            boatDirection = Direction.Vertical;
        } else {
            boatDirection = Direction.Horizontal;
        }

        EnterCell(null, cursorPos.x, cursorPos.y);
    }

    public static void main(String[] args) {
        launch();
    }
}