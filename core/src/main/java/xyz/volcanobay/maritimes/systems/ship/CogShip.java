package xyz.volcanobay.maritimes.systems.ship;

import xyz.volcanobay.maritimes.systems.city.City;

public class CogShip extends Ship{
    public CogShip(String name) {
        super(name);
    }

    @Override
    public float getMaxSpeed() {
        return 0.99f;
    }

    @Override
    public float getAcceleration() {
        return 0.05f;
    }

    @Override
    public float getStorageSpace() {
        return 35;
    }
}
