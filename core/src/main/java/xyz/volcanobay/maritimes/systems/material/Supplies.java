package xyz.volcanobay.maritimes.systems.material;

import java.util.Objects;

public class Supplies {
    private Material material = null;
    private int count = 0;
    public boolean buy = false;
    public  boolean sell = false;
    public float calculatedValue = 0;

    public Supplies(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public void add(int amount) {
        this.count += amount;
    }

    public int getCount() {
        return count;
    }

    public void setCalculatedValue(float calculatedValue) {
        this.calculatedValue = calculatedValue;
    }

    public float getCalculatedValue() {
        return calculatedValue;
    }

    @Override
    public String toString() {
        return "Supplies["+material.getName()+", "+count+"]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Supplies supplies) {
            return supplies.material.equals(material);
        }
        if (obj instanceof Material material) {
            return Objects.equals(this.material.getName(), material.getName());
        }
        return super.equals(obj);
    }
}
