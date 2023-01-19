package net.info420.bataillenavale;

public class IntelligenceArtificielle {

    IntelligenceArtificielle()
    {
        //La difficulté est facile par défaut
        this.Difficulty_Hard = false;
        this.tableauTirs = BatailleNavale.grilleJoueur;
    }

    IntelligenceArtificielle(boolean difficulty)
    {
        this.Difficulty_Hard = difficulty;
        this.tableauTirs = BatailleNavale.grilleJoueur;
    }


    //Difficulté du jeu. L'ordinateur joue mieux si cette variable est true
    public boolean Difficulty_Hard;
    //Grille qui contient tous les endroits où l'ordinateur a déjà tiré
    public int[][] tableauTirs;


    public Vector2 tirer()
    {
        Vector2 coords = new Vector2();

        return  coords;
    }


}
