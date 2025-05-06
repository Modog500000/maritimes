package xyz.volcanobay.maritimes.systems;

import xyz.volcanobay.maritimes.systems.material.Material;
import xyz.volcanobay.maritimes.systems.material.Supplies;

import java.util.ArrayList;
import java.util.List;

public class MaterialSystem {

    public static final List<Material> materialList = new ArrayList<>();

    public static final Material GOLD = registerMaterial(new Material("gold",5,240));
    public static final Material SILVER = registerMaterial(new Material("silver",5,80));
    public static final Material FOOD = registerMaterial(new Material("food",1,1));
    public static final Material SALT = registerMaterial(new Material("salt",2,2));
    public static final Material PEPPER = registerMaterial(new Material("pepper",2,92));
    public static final Material CINNAMON = registerMaterial(new Material("cinnamon",2,4));

    private static Material registerMaterial(Material material) {
        materialList.add(material);
        return material;
    }

    public static void register() {

    }

    public static Supplies findMatchingSupplies(List<Supplies> suppliesList, Material material) {
        for (Supplies supplies : suppliesList) {
            if (supplies.equals(material)) {
                return supplies;
            }
        }
        return null;
    }
}
