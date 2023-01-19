package net.info420.bataillenavale;

public class bateauxEnnemis {
    Vector2 position;
    Direction direction;

    public bateauxEnnemis(Vector2 pos, Direction dir){
        this.position = pos;
        this.direction = dir;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }
}


