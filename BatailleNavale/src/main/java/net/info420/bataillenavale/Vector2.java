package net.info420.bataillenavale;
/**
 * Classe Vector2 qui correspond à une position 2D (x, y)
 */

public class Vector2 {
    /**
     * Int, correspond à la position horizontale
     */
    public int x;

    /**
     * Int, correspond à la position verticale
     */
    public int y;

    /**
     * Constructeur de Vector2
     * @param xPos Position horizontale
     * @param yPos Position verticale
     */
    public Vector2(int xPos, int yPos) {
        x = xPos;
        y = yPos;
    }

    /**
     * Méthode qui retourne la position horizontale
     * @return La position horizontale
     */
    public int GetX() {
        return this.x;
    }

    /**
     * Méthode qui retourne la position verticale
     * @return La position verticale
     */
    public int GetY() {
        return this.y;
    }
}
