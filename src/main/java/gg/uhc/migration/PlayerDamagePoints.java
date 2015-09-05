package gg.uhc.migration;

public class PlayerDamagePoints {

    protected int points = 0;

    public void increment() {
        points++;
    }

    public int get() {
        return points;
    }

    public void remove(int amount) {
        points -= amount;
    }
}
