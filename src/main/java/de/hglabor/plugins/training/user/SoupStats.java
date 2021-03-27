package de.hglabor.plugins.training.user;

public class SoupStats {
    private int soupsEaten;
    private int soupsDropped;

    public int getSoupsEaten() {
        return soupsEaten;
    }

    public int getSoupsDropped() {
        return soupsDropped;
    }

    public void increaseSoupsEaten() {
        soupsEaten++;
    }

    public void increaseSoupsDropped() {
        soupsDropped++;
    }

    public void reset() {
        soupsEaten = 0;
        soupsDropped = 0;
    }
}
