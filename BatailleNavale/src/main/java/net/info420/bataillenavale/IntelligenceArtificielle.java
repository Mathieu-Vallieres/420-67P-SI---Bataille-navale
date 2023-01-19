package net.info420.bataillenavale;

public class IntelligenceArtificielle {

    IntelligenceArtificielle()
    {
        //La difficulté est facile par défaut
        this.difficulty_Hard = false;
        this.tableauTirs = BatailleNavale.grilleJoueur;
    }

    IntelligenceArtificielle(boolean difficulty)
    {
        this.difficulty_Hard = difficulty;
        this.tableauTirs = BatailleNavale.grilleJoueur;
    }

    public void setDifficulty_Hard()
    {
        this.difficulty_Hard = true;
    }

    public void setDifficulty_Easy()
    {
        this.difficulty_Hard = false;
    }

    //Difficulté du jeu. L'ordinateur joue mieux si cette variable est true
    public boolean difficulty_Hard;

    //Grille qui contient tous les endroits où l'ordinateur a déjà tiré
    public int[][] tableauTirs;

    //Permet de générer des nombres aléatoires
    Random rand = new Random();

    //Sert à stocker le dernier tir pour le tir intelligent
    public Vector2 dernierTir;

    //Entre en mode tir intelligent
    public boolean tirIntelligent;

    //Stocke l'ID du bateau qui est visé par le tir intelligent
    public int IDBateauTirIntelligent;

    //Directions possibles pour les prochains tirs intelligents
    //1 Haut, 2 Gauche, 3 Bas, 4 Droite
    public int directionTir;
    public int derniereDirection;

    /**
     * Permet de générer des coordonnées aléatoires selon le mode de difficulté
     *
     * @return un vecteur contenant les coordonnées générées
     */
    public Vector2 genererCoordsAleatoire()
    {
        Vector2 coords;

        //Tire différament selon la difficulté
        if(difficulty_Hard)
        {
            /*
                S'assure que l'ordinateur tire sur une case sur 2, par exemple :

                    1 2 3 4 5 6 7 8 9 10
                  A X O X O X O X O X O
                  B O X O X O X O X O X
                  C X O X O X O X O X O
                  D O X O X O X O X O X
                  E X O X O X O X O X O
                  F O X O X O X O X O X
                  G X O X O X O X O X O
                  H O X O X O X O X O X
                  I X O X O X O X O X O
                  J O X O X O X O X O X

                Les X représentent ici les coups possibles.
                Toutes les coordonnées doivent avoir 2 nombres impairs ou 2 nombres pairs.
             */

            int x;
            int y;

            x = randRange(0,10);
            y = randRange(0, 10);

            if(x % 2 = 0) //X est pair
            {
                while(y % 2 != 0)
                {
                    //Génère des y jusqu'à ce qu'ils soient pairs
                    y = randRange(0, 10);
                }
            }
            else //X est impair
            {
                while(y % 2 != 1)
                {
                    //Génère des y jusqu'à ce qu'ils soient impairs
                    y = randRange(0, 10);
                }
            }

            coords = new Vector2(x, y);
        }
        else
        {
            //Génère 2 coordonnées aléatoires entre 0 et 9
            coords = new Vector2(randRange(0,10), randRange(0,10));
        }

        return  coords;
    }

    //Renvoie les coordonnées où l'ordi a tiré pour pouvoir faire l'update graphique dans la grille
    public Vector2 tirer()
    {
        if(tirIntelligent)
        {
            /*
            //Par défaut, le tir intelligent commence par la case en haut de celle qui a été touchée
            directionTir = 1;

            //Élimine les directions de tir impossibles si le bateau est collé sur un des côtés de la grille




            switch (directionTir)
            {
                case 1:
                {

                }
                case 2:
                {

                }
                case 3:
                {

                }
                case 4:
                {

                }
            }



            if(couler(tableauTirs, IDBateauTirIntelligent))
            {
                //5a. Si le bateau est coulé, désactiver le tir intelligent
                tirIntelligent = false;
            }
             */
        }
        else
        {
            do {
                Vector2 coords = genererCoordsAleatoire();
            }
            //S'assure que le programme ne tire pas 2 fois au même endroit
            while (tableauTirs[coords.GetX()][coords.GetY()] != 6 && tableauTirs[coords.GetX()][coords.GetY] != 7);

            //À partir d'ici on peut tirer
            if(tableauTirs[coords.GetX()][coords.GetY()] > 0)
            {
                //Bateau touché :
                //1. Stocke l'ID du bateau pour pouvoir le cibler avec le tir intelligent
                IDBateauTirIntelligent = tableauTirs[coords.GetX()][coords.GetY()];
                //2. Marque la case comme une section de bateau touchée
                tableauTirs[coords.GetX()][coords.GetY()] = 6;
                //3. Stocke la position du dernier tir pour le tir intelligent
                dernierTir = new Vector2(coords.GetX(),coords.GetY());
                //4. Tester si le bateau est coulé
                if(couler(tableauTirs, IDBateauTirIntelligent))
                {
                    //5a. Si le bateau est coulé, désactiver le tir intelligent
                    tirIntelligent = false;
                }
                else
                {
                    //5b. Si le bateau n'est pas coulé, activer le tir intelligent
                    tirIntelligent = true;
                }
            }
            else
            {
                //Atterri dans l'eau
                tableauTirs[coords.GetX()][coords.GetY] = 7;
            }
        }

        return coords;
    }

    /**
     * Retourne un nombre aleatoire entre a inclus et b exclus
     */
    public static int randRange(int a, int b)
    {
        return rand.nextInt(b - a) + a;
    }

    /**
     * Vérifie si un bateau est coulé (n'est plus présent dans la grille)
     *
     * @param grille Grille dans laquelle la vérification est effectuée
     * @param bateau Bateau qui est vérifié
     * @return Un booléen qui sera vrai si un bateau n'apparait plus dans la grille
     */
    public static boolean couler(int [][] grille, int bateau)
    {
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j ++)
            {
                if(grille[i][j] == bateau)
                {
                    //La fonction indique que le bateau n'est pas coulé lorsqu'elle trouve au moins une fois son numéro dans la grille
                    return false;
                }
            }
        }

        //Le bateau a été coulé; son chiffre n'apparait pas dans la grille
        return true;
    }

}
