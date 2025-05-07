package xyz.volcanobay.maritimes.systems.ship;

public class TreasureShip extends Ship{
    public TreasureShip(String name) {
        super(name);
    }

    @Override
    public float getMaxSpeed() {
        return 2f;
    }

    @Override
    public float getAcceleration() {
        return 0.1f;
    }

    @Override
    public float getStorageSpace() {
        return 200;
    }

    @Override
    public int cost() {
        return 30000;
    }
}
