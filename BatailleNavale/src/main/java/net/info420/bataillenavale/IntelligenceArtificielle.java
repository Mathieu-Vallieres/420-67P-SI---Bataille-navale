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

    //Difficulté du jeu. L'ordinateur joue mieux si cette variable est true
    public boolean difficulty_Hard;
    //Grille qui contient tous les endroits où l'ordinateur a déjà tiré
    public int[][] tableauTirs;
    //Permet de générer des nombres aléatoires
    Random rand = new Random();

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

    /**
     * Retourne un nombre aleatoire entre a inclus et b exclus
     */
    public static int randRange(int a, int b)
    {
        return rand.nextInt(b - a) + a;
    }

    /**
     * Fonction qui retourne un tableau qui contient les coordonn�es du tir al�atoire de l'ordinateur
     *
     * @return un tableau contenant la ligne et la colonne du tir de l'ordinateur
     */
    public static int[] tirOrdinateur()
    {
        int ligne = randRange(0,10);
        int colonne = randRange(0,10);

        int[] tableau = {ligne, colonne};

        return tableau;
    }

    /**
     * V�rifie si un bateau est coul� (n'est plus pr�sent dans la grille)
     *
     * @param grille Grille dans laquelle la v�rification est effectu�e
     * @param bateau Bateau qui est v�rifi�
     * @return Un bool�en qui sera vrai si un bateau n'apparait plus dans la grille
     */
    public static boolean couler(int [][] grille, int bateau)
    {
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j ++)
            {
                if(grille[i][j] == bateau)
                {
                    //La fonction indique que le bateau n'est pas coul� lorsqu'elle trouve au moins une fois son num�ro dans la grille
                    return false;
                }
            }
        }

        //Le bateau a �t� coul�; son chiffre n'apparait pas dans la grille
        return true;
    }

}
