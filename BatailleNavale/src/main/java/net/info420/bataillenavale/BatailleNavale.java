package net.info420.bataillenavale;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static javafx.application.Application.launch;

public class BatailleNavale {
    public static int WindowHeight = 560;
    public static int WindowWidth = 920;
    public static Stage mainWindow;
    public static Pane[][] playerClickPanes;
    public static Pane[][] computerClickPanes;
    public static GridPane playerGrid;
    public static GridPane computerGrid;
    public static Image BoatHeadIMG;
    public static Image BoatBodyIMG;
    public static Image BoatTailIMG;
    public static Label StatusLabel;

    public static ArrayList<ImageView> previewImages;
    public static ArrayList<ImageView> boatImages;
    public static ArrayList<ImageView> brokenBoatImages;

    public static Direction boatDirection = Direction.Horizontal;
    public static Vector2 cursorPos;

    /**Grille contenant les informations sur les bateaux de l'ordinateur*/
    public static int [][] grilleOrdi = new int [10][10];

    /**Grille contenant les informations sur les bateaux du joueur*/
    public static int [][] grilleJoueur = new int [10][10];

    public static int placementBateau = 1;

    public static ColorAdjust previewEffect;
    public static ColorAdjust normalEffect;
    public static ColorAdjust hitEffect;

    public static char Gagnant;

    public static GameStates gameState = GameStates.PLACEMENT_BATEAU;

    public static boolean HardMode = false;

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

    /**
     * Déclaration d'un Random utilisé pour avoir des nombres aléatoires
     */
    public static Random rand = new Random();

    /**
     * HashMap permettant de lier d'id du bateau avec sa position et sa direction (Classe bateauxEnnemis)
     */
    @SuppressWarnings("serial")
    public static HashMap<Integer, bateauxEnnemis> positionBateauxEnnemis;

    public static double map(double value, double start, double stop, double targetStart, double targetStop) {
        return targetStart + (targetStop - targetStart) * ((value - start) / (stop - start));
    }

    public BatailleNavale(boolean modeDifficile) {
        HardMode = modeDifficile;
        CreeMenu();
    }

    public void CreeMenu() {

        previewEffect = new ColorAdjust();
        previewEffect.setBrightness(0.5);

        normalEffect = new ColorAdjust();

        hitEffect = new ColorAdjust();
        hitEffect.setHue(map(187, 0, 360, -1, 1));
        hitEffect.setSaturation(1);
        hitEffect.setBrightness(-0.35);

        try {
            BoatHeadIMG = new Image(new FileInputStream(System.getProperty("user.dir") + "/BatailleNavale/src/images/head.png"));
            BoatBodyIMG = new Image(new FileInputStream(System.getProperty("user.dir") + "/BatailleNavale/src/images/body.png"));
            BoatTailIMG = new Image(new FileInputStream(System.getProperty("user.dir") + "/BatailleNavale/src/images/tail.png"));
        } catch(Exception ex) {}

        previewImages = new ArrayList<ImageView>();
        boatImages = new ArrayList<ImageView>();
        brokenBoatImages = new ArrayList<ImageView>();

        mainWindow = new Stage();

        mainWindow.setTitle("Battleship");
        mainWindow.setMinHeight(WindowHeight);
        mainWindow.setMinWidth(WindowWidth);
        mainWindow.setResizable(false);

        mainWindow.setOnHiding(e -> {
            MenuPrincipal.mainWindow.show();
            mainWindow.close();
        });

        StackPane root = new StackPane();

        MenuBar menuBar = new MenuBar();
        root.getChildren().add(menuBar);
        Menu optionMenu = new Menu("Options");
        menuBar.getMenus().add(optionMenu);

        MenuItem newGame = new MenuItem("Nouvelle partie");

        newGame.setOnAction(e -> {
            initJeu();
        });

        MenuItem retour = new MenuItem("Retour au menu principal");

        retour.setOnAction(e -> {
            MenuPrincipal.mainWindow.show();
            mainWindow.close();
        });

        MenuItem quitter = new MenuItem("Quitter");

        quitter.setOnAction(e -> {
            System.exit(0);
        });

        optionMenu.getItems().addAll(newGame, retour, quitter);

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

        mainWindow.setScene(primaryScene);
        mainWindow.show();

        primaryScene.setOnMousePressed(e -> {
            if(e.getButton() == MouseButton.SECONDARY) {
                ToggleVerticalHorizontal();
            }
        });

        StackPane playerStack = new StackPane();
        playerGrid = new GridPane();
        playerStack.getChildren().add(playerGrid);

        StackPane computerStack = new StackPane();
        computerGrid = new GridPane();
        computerStack.getChildren().add(computerGrid);

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

        StatusLabel = new Label();
        rootGrid.add(StatusLabel, 0, 2);

        initJeu();
    }

    public static void initJeu() {
        positionBateauxEnnemis = new HashMap<>();

        remplirGrillesDeZeros();

        for(ImageView image : previewImages) {
            if(image.getParent() != null) {
                ((Pane) image.getParent()).getChildren().remove(image);
            }
        }
        previewImages.clear();

        for(ImageView image : boatImages) {
            if(image.getParent() != null) {
                ((Pane) image.getParent()).getChildren().remove(image);
            }
        }
        boatImages.clear();

        playerClickPanes = new Pane[10][10];
        computerClickPanes = new Pane[10][10];

        playerGrid.getChildren().clear();
        computerGrid.getChildren().clear();

        SetupGrid(true);
        SetupGrid(false);

        initGrilleOrdi();
        gameState = GameStates.PLACEMENT_BATEAU;
        placementBateau = 1;
        UpdateStatusLabel();
    }

    public static void UpdateStatusLabel() {
        if(gameState == GameStates.PLACEMENT_BATEAU) {
            StatusLabel.setText("Placement de: " + getBoatName() + " (" + grandeurBateaux.get(placementBateau) + " cases)");
        } else if(gameState == GameStates.JEU) {
            StatusLabel.setText("Jeu en cours");
        }
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

    /**
     * Fonction qui permet d'initialiser la grille de l'ordinateur avec des bateaux aléatoires
     */
    public static void initGrilleOrdi() {
        // Déclaration des variables
        int ligne;
        int colonne;
        Direction direction;

        // Déclaration de la variable posOk qui sert à déterminer si le bateau peut être placé à l'endroit voulu
        boolean posOk = false;

        // On boucle à travers la HashMap contenant l'information des bateaux
        for (Map.Entry<String, Integer> bateau : idBateaux.entrySet()) {
            do {
                // Si un nombre aléatoire entre 1 et 10 est supérieur à 5, la direction est horizontal
                // Il y a probablement une meilleure méthode pour un nombre aléatoire ayant 50% de chance, mais cela fonctionne bien
                if(randRange(1, 10) > 5) {
                    direction = Direction.Horizontal;
                } else { // Sinon la direction est vertical
                    direction = Direction.Vertical;
                }

                // On obtient une position de ligne entre 0 et 9
                ligne = randRange(0, 9);

                // On obtient une position de colonne entre 0 et 9
                colonne = randRange(0, 9);

                // On regarde si la position aléatoire est valide
                posOk = PosOk(grilleOrdi, ligne, colonne, direction, grandeurBateaux.get(bateau.getValue()));

                // Si la position est valide, on place le bateau
                if(posOk) {
                    placerBateau(grilleOrdi, ligne, colonne, direction, bateau.getValue(), false);
                }
            } while(!posOk); // On boucle tant que la position du bateau n'est pas correct
        }
        afficherGrille(grilleOrdi);
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
    public static void placerBateau(int[][] grille, int ligne, int colonne, Direction direction, int idBateau, boolean joueur) {
        if(joueur) {
            for (ImageView image : previewImages) {
                if (image.getParent() != null) {
                    ((Pane) image.getParent()).getChildren().remove(image);
                }
            }
            previewImages.clear();
        }

        if(!joueur) {
            positionBateauxEnnemis.put(idBateau, new bateauxEnnemis(new Vector2(colonne, ligne), direction));
        }

        int rotation = 0;
        if(direction == Direction.Horizontal) {
            rotation = 90;
        }

        ImageView head = new ImageView(BoatHeadIMG);
        head.setEffect(normalEffect);
        head.setRotate(rotation);

        ImageView tail = new ImageView(BoatTailIMG);
        tail.setEffect(normalEffect);
        tail.setRotate(rotation);

        if(direction == Direction.Horizontal) { // Si la direction est horizontal
            // On boucle à partir de la colonne jusqu'à la colonne additionné de la grandeur du bateau
            for(int i = colonne; i < colonne + grandeurBateaux.get(idBateau); i++) {
                // On met l'ID du bateau sur les bonnes cases
                grille[ligne][i] = idBateau;
            }
            if(joueur) {
                playerClickPanes[colonne][ligne].getChildren().add(tail);
                boatImages.add(tail);

                if (grandeurBateaux.get(placementBateau) > 2) {
                    for (int i = colonne + 1; i < colonne + (grandeurBateaux.get(placementBateau)) - 1; i++) {
                        ImageView body = new ImageView(BoatBodyIMG);
                        body.setEffect(normalEffect);
                        body.setRotate(rotation);
                        playerClickPanes[i][ligne].getChildren().add(body);
                        boatImages.add(body);
                    }
                }

                playerClickPanes[colonne + (grandeurBateaux.get(placementBateau) - 1)][ligne].getChildren().add((head));
                boatImages.add(head);
            }
        } else if(direction == Direction.Vertical) { // Si la direction est verticale
            // On boucle à partir de la ligne jusqu'à la ligne additionné de la grandeur du bateau
            for(int i = ligne; i < ligne + grandeurBateaux.get(idBateau); i++) {
                // On met l'ID du bateau sur les bonnes cases
                grille[i][colonne] = idBateau;
            }

            if(joueur) {
                playerClickPanes[colonne][ligne].getChildren().add(head);
                boatImages.add(head);

                if (grandeurBateaux.get(placementBateau) > 2) {
                    for (int i = ligne + 1; i < ligne + (grandeurBateaux.get(placementBateau)) - 1; i++) {
                        ImageView body = new ImageView(BoatBodyIMG);
                        body.setEffect(normalEffect);
                        body.setRotate(rotation);
                        playerClickPanes[colonne][i].getChildren().add(body);
                        boatImages.add(body);
                    }
                }

                playerClickPanes[colonne][ligne + (grandeurBateaux.get(placementBateau) - 1)].getChildren().add((tail));
                boatImages.add(tail);
            }
        }

        if(joueur) {
            placementBateau++;

            if (placementBateau > 5) {
                gameState = GameStates.JEU;
            }

            UpdateStatusLabel();
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
                if(grille[i][j] != 0 && grille[i][j] != 6 && grille[i][j] != 7) {
                    return false;
                }
            }
        }

        // Toutes les cases sont de l'eau (0) et des bateaux touchées (6), la grille a perdu, on retourne false
        return true;
    }

    private static Pane CreateClickPane(int x, int y, boolean isComputer) {
        Pane newPane = new Pane();
        newPane.setMinSize(40, 40);
        //newPane.setStyle("-fx-background-color: white; -fx-border-color: black;");
        newPane.setStyle("-fx-background-image: url('http://clipart-library.com/img/1745178.gif'); -fx-opacity: 0.80; -fx-background-size: 100; -fx-border-color: black;");

        if(isComputer) {
            computerGrid.add(newPane, x + 1, y + 1);
        } else {
            playerGrid.add(newPane, x + 1, y + 1);
        }

        newPane.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY)
                ClickOnCell(newPane, x, y, isComputer);
        });

        if(!isComputer) {
            newPane.setOnMouseEntered(e -> {
                EnterCell(newPane, x, y);
            });
        }

        return newPane;
    }

    /**
     * Fonction qui permet de tirer une torpille sur une grille
     * @param grille Grille où nous voulons tirer la torpille
     * @param ligne Ligne où nous voulons tirer la torpille
     * @param colonne Colonne où nous voulons tirer la torpille
     */
    public static void tirerTorpille(int[][] grille, int ligne, int colonne, boolean joueur) {
        // Déclaration des variables
        boolean touche = false;
        boolean coule = false;
        int boatID = 0;

        // Si la position n'est pas de l'eau (0) ou un bateau déjà touché (6)
        if(grille[ligne][colonne] != 0 && grille[ligne][colonne] != 6 && grille[ligne][colonne] != 7) {
            // La torpille touche un bateau
            touche = true;

            // Si la torpille a touché un bateau
            if(touche) {
                // On déclare et initialise une variable "nombreBateauTouche" qui nous permettra de savoir
                // combien de case il reste au bateau qu'on vient de toucher
                int nombreBateauTouche = 0;

                // On boucle à travers les lignes
                for(int i = 0; i < 10; i++) {
                    // On boucle à travers les colonnes
                    for(int j = 0; j < 10; j++) {
                        // Si la case est similaire au bateau que nous venons de toucher
                        if(grille[i][j] == grille[ligne][colonne]) {
                            // On incrémente le nombre de case du bateau que nous venons de toucher de 1
                            nombreBateauTouche++;
                        }
                    }
                }

                // Si le nombre de case touché - 1 est 0, cela veut dire que nous venons de toucher la dernière case du bateau
                if(nombreBateauTouche - 1 <= 0) {
                    // Le bateau est coulé
                    coule = true;
                }

                boatID = grille[ligne][colonne];

                // On met la case de la torpille comme étant touché (6)
                grille[ligne][colonne] = 6;
            }
        } else {
            if(grille[ligne][colonne] == 0) { // Si la torpille est dans l'eau, on le montre à l'utilisateur
                System.out.println("Tir dans l'eau!");
                grille[ligne][colonne] = 7;
                if(joueur) {
                    //playerClickPanes[colonne][ligne].setStyle("-fx-background-color: grey; -fx-border-color: black;");
                    playerClickPanes[colonne][ligne].setStyle("-fx-background-image: url('http://clipart-library.com/img/1745178.gif') url('https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/Red_X.svg/1024px-Red_X.svg.png'); -fx-opacity: 0.80; -fx-background-size: 100, 40; -fx-border-color: red; -fx-border-width: 3;");
                } else {
                    //computerClickPanes[colonne][ligne].setStyle("-fx-background-color: grey; -fx-border-color: black;");
                    computerClickPanes[colonne][ligne].setStyle("-fx-background-image: url('http://clipart-library.com/img/1745178.gif'), url('https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/Red_X.svg/1024px-Red_X.svg.png'); -fx-opacity: 0.80; -fx-background-size: 100, 40; -fx-border-color: red; -fx-border-width: 3;");
                }
            } else if(grille[ligne][colonne] == 6) { // Si la torpille est sur un bateau déjà touché, on le montre à l'utilisateur
                System.out.println("Bateau déjà touché!");
            }
        }

        if(joueur) {
            ObservableList<Node> childs = playerClickPanes[colonne][ligne].getChildren();

            for(Node child : childs) {
                if(child instanceof ImageView) {
                    child.setEffect(hitEffect);
                }
            }
        } else {
            if(touche && !coule) {
                //computerClickPanes[colonne][ligne].setStyle("-fx-background-color: red; -fx-border-color: black;");
                computerClickPanes[colonne][ligne].setStyle("-fx-background-image: url('http://clipart-library.com/img/1745178.gif'), url('https://static.wikia.nocookie.net/minecraft_gamepedia/images/6/65/Fire_BE.gif/revision/latest?cb=20200325141322'); -fx-opacity: 0.80; -fx-background-position: center; -fx-background-size: 100, 35; -fx-border-color: lime; -fx-border-width: 3;");
            } else if(touche && coule) {
                Direction boatDirect = positionBateauxEnnemis.get(boatID).getDirection();
                Vector2 boatPos = positionBateauxEnnemis.get(boatID).getPosition();
                int boatSize = grandeurBateaux.get(boatID);

                int rotation = 0;
                if(boatDirect == Direction.Horizontal) {
                    rotation = 90;
                }

                ImageView head = new ImageView(BoatHeadIMG);
                head.setEffect(hitEffect);
                head.setRotate(rotation);

                ImageView tail = new ImageView(BoatTailIMG);
                tail.setEffect(hitEffect);
                tail.setRotate(rotation);

                if(boatDirect == Direction.Horizontal) {
                    for(int i = boatPos.x; i < boatPos.x + boatSize; i++) {
                        //computerClickPanes[i][ligne].setStyle("-fx-background-color: white; -fx-border-color: black;");
                        computerClickPanes[i][ligne].setStyle("-fx-background-image: url('http://clipart-library.com/img/1745178.gif'); -fx-opacity: 0.80; -fx-background-size: 100; -fx-border-color: lime; -fx-border-width: 3;");
                    }

                    computerClickPanes[boatPos.x][ligne].getChildren().add(tail);
                    brokenBoatImages.add(tail);

                    if (boatSize > 2) {
                        for (int i = boatPos.x + 1; i < boatPos.x + boatSize - 1; i++) {
                            ImageView body = new ImageView(BoatBodyIMG);
                            body.setEffect(hitEffect);
                            body.setRotate(rotation);
                            computerClickPanes[i][ligne].getChildren().add(body);
                            brokenBoatImages.add(body);
                        }
                    }

                    computerClickPanes[boatPos.x + boatSize - 1][ligne].getChildren().add((head));
                    brokenBoatImages.add(head);
                } else {
                    for(int i = boatPos.y; i < boatPos.y + boatSize; i++) {
                        //computerClickPanes[colonne][i].setStyle("-fx-background-color: white; -fx-border-color: black;");
                        computerClickPanes[colonne][i].setStyle("-fx-background-image: url('http://clipart-library.com/img/1745178.gif'); -fx-opacity: 0.80; -fx-background-size: 100; -fx-border-color: lime; -fx-border-width: 3");
                    }

                    computerClickPanes[colonne][boatPos.y].getChildren().add(head);
                    brokenBoatImages.add(head);

                    if (boatSize > 2) {
                        for (int i = boatPos.y + 1; i < boatPos.y + boatSize - 1; i++) {
                            ImageView body = new ImageView(BoatBodyIMG);
                            body.setEffect(hitEffect);
                            body.setRotate(rotation);
                            computerClickPanes[colonne][i].getChildren().add(body);
                            brokenBoatImages.add(body);
                        }
                    }

                    computerClickPanes[colonne][boatPos.y + boatSize - 1].getChildren().add((tail));
                    brokenBoatImages.add(tail);
                }
            }
        }

        if(aPerdu(grilleOrdi)) {
            gameState = GameStates.FINI;
            Gagnant = 'j';
            messageFinPartie();
            return;
        } else if(aPerdu(grilleJoueur)) {
            gameState = GameStates.FINI;
            Gagnant = 'o';
            messageFinPartie();
            return;
        }

        if(!joueur) {
            tirerOrdi();
        }
    }

    private static void SetupGrid(boolean isComputer) {
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

    private static void ClickOnCell(Pane pane, int x, int y, boolean isComputer) {
        if(gameState == GameStates.PLACEMENT_BATEAU) {
            if(!isComputer) {
                if (PosOk(grilleJoueur, y, x, boatDirection, grandeurBateaux.get(placementBateau))) {
                    placerBateau(grilleJoueur, y, x, boatDirection, placementBateau, true);
                }
            }
        } else if(gameState == GameStates.JEU) {
            if(isComputer) {
                tirerTorpille(grilleOrdi, y, x, false);
            }
        }
        System.out.println("X: " + x + " " + "Y: " + y + " Computer: " + isComputer);
    }

    private static void EnterCell(Pane pane, int x, int y) {
        for(ImageView image : previewImages) {
            if(image.getParent() != null) {
                ((Pane) image.getParent()).getChildren().remove(image);
            }
        }

        if(gameState != GameStates.PLACEMENT_BATEAU) {
            return;
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

        ImageView tail = new ImageView(BoatTailIMG);
        tail.setEffect(previewEffect);
        tail.setRotate(rotation);

        if(boatDirection == Direction.Horizontal) {
            if(PosOk(grilleJoueur, y, x, boatDirection, grandeurBateaux.get(placementBateau))) {
                try {
                    playerClickPanes[x][y].getChildren().add(tail);
                    previewImages.add(tail);

                    if(grandeurBateaux.get(placementBateau) > 2) {
                        for(int i = x + 1; i < x + (grandeurBateaux.get(placementBateau)) - 1; i++) {
                            ImageView body = new ImageView(BoatBodyIMG);
                            body.setEffect(previewEffect);
                            body.setRotate(rotation);
                            playerClickPanes[i][y].getChildren().add(body);
                            previewImages.add(body);
                        }
                    }

                    playerClickPanes[x + (grandeurBateaux.get(placementBateau) - 1)][y].getChildren().add((head));
                    previewImages.add(head);
                } catch (Exception ex) {}
            }
        } else {
            if(PosOk(grilleJoueur, y, x, boatDirection, grandeurBateaux.get(placementBateau))) {
                try {
                    playerClickPanes[x][y].getChildren().add(head);
                    previewImages.add(head);

                    if(grandeurBateaux.get(placementBateau) > 2) {
                        for(int i = y + 1; i < y + (grandeurBateaux.get(placementBateau)) - 1; i++) {
                            ImageView body = new ImageView(BoatBodyIMG);
                            body.setEffect(previewEffect);
                            body.setRotate(rotation);
                            playerClickPanes[x][i].getChildren().add(body);
                            previewImages.add(body);
                        }
                    }

                    playerClickPanes[x][y + (grandeurBateaux.get(placementBateau) - 1)].getChildren().add((tail));
                    previewImages.add(tail);
                } catch (Exception ex) {}
            }
        }
    }

    public static void afficherGrille(int[][] grille) {
        // Print des espaces pour l'alignement
        System.out.print("    ");

        // Boucle qui permet d'afficher les lettres de A à I
        for(int i = 0; i < 10; i++) {
            System.out.print(Character.toString(i + 65) + "  ");
        }

        // Retour à la ligne (newline)
        System.out.print("\n");

        // Boucle à travers les lignes
        for(int i = 0; i < 10; i++) {
            // Print le numero de ligne
            // La ligne #10 à une espace de moins pour l'alignement
            System.out.print(i + 1 + (i == 9 ? "  " : "   "));

            // Boucle à travers les colonnes
            for(int j = 0; j < 10; j++) {
                // Print le contenu de la grille
                System.out.print(grille[i][j] + "  ");
            }

            // Retour à la ligne (newline)
            System.out.print("\n");
        }
    }

    private static void tirerOrdi() {
        Vector2 position = new Vector2(randRange(0, 9), randRange(0, 9));
        tirerTorpille(grilleJoueur, position.y, position.x, true);
    }

    private void ToggleVerticalHorizontal() {
        if(boatDirection == Direction.Horizontal) {
            boatDirection = Direction.Vertical;
        } else {
            boatDirection = Direction.Horizontal;
        }

        EnterCell(null, cursorPos.x, cursorPos.y);
    }

    public static void messageFinPartie(){
        Alert message;
        if (Gagnant == 'j'){
            message = new Alert(Alert.AlertType.CONFIRMATION, "Bravo! Vous avez gagné!", new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
            message.setTitle("Victoire!");
            message.setHeaderText("Victoire!");
            message.show();
        }else if(Gagnant == 'o') {
            message = new Alert(Alert.AlertType.ERROR, "Dommage! Vous avez perdu!", new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
            message.setTitle("Défaite!");
            message.setHeaderText("Défaite!");
            message.show();
        }
    }

    /**
     * Fonction qui permet d'obtenir un nombre entier entre a et b
     * @param a Le plus bas nombre possible
     * @param b Le plus haut nombre possible
     * @return Un nombre entier entre a et b
     */
    public static int randRange(int a, int b) {
        return rand.nextInt(b-a)+a;
    }
}