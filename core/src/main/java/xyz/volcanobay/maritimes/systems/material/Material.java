package xyz.volcanobay.maritimes.systems.material;

import com.badlogic.gdx.graphics.Texture;
import xyz.volcanobay.maritimes.core.RenderSystem;

import java.util.Objects;

public class Material {
    private Texture itemTexture;
    private Texture boxTexture;
    private String name;
    private float weight;
    private float value;

    public Material(String name, float weight, float value) {
        this.itemTexture = RenderSystem.getTexture("assets/textures/materials/" + name + ".png");
        this.boxTexture = RenderSystem.getTexture("assets/textures/materials/" + name + "_box.png");
        this.name = name;
        this.weight = weight;
        this.value = value;
    }

    public float getWeight() {
        return weight;
    }

    public String getName() {
        return name;
    }

    public Texture getItemTexture() {
        return itemTexture;
    }

    public Texture getBoxTexture() {
        return boxTexture;
    }

    public float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Material[" +name+"]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Material material) {
            return Objects.equals(material.getName(), name);
        }
        if (obj instanceof Supplies supplies) {
            return Objects.equals(supplies.getMaterial().getName(), name);
        }
        return super.equals(obj);
    }
}
