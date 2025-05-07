package xyz.volcanobay.maritimes.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import xyz.volcanobay.maritimes.Maritimes;
import xyz.volcanobay.maritimes.core.RenderSystem;
import xyz.volcanobay.maritimes.systems.input.Bounds;
import xyz.volcanobay.maritimes.systems.input.Button;
import xyz.volcanobay.maritimes.systems.material.Material;
import xyz.volcanobay.maritimes.systems.material.Supplies;
import xyz.volcanobay.maritimes.systems.ship.Ship;
import xyz.volcanobay.maritimes.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class TradingSystem {
    public static float money = 0f;

    private static Material heldMaterial = null;

    private static List<Button> buttons = new ArrayList<>();

    public static void renderFirst(float partialTick) {
        float x = 1;
        buttons.clear();

        buttons.add(new Button(28,30,1,3));
        buttons.getLast().setAnyAction(() -> {
            RenderSystem.shipBuilder = true;
        });

        for (Material material : MaterialSystem.materialList) {
            RenderSystem.batch.draw(material.getBoxTexture(), x, 12.5f, 1, 2);
            buttons.add(new Button(x, x + 1, 12.5f, 14.5f));
            buttons.getLast().setAnyAction(() -> {
                if (money-material.getValue() >= 0) {
                    heldMaterial = material;
                    money -= heldMaterial.getValue();
                }
            });
            String text = String.valueOf(material.getValue());
            Color color = new Color(1,1,1,1);
            if (money-material.getValue() < 0) {
                color = new Color(1,0.3f,0.3f,1);
            }

            if (InputSystem.mouseInBound(buttons.getLast())) {
                RenderSystem.drawText(text, Mth.map(InputSystem.mouse.x, 0, 30, -600, 600), Mth.map(InputSystem.mouse.y, 0, 26, -310, 740), color);
            }
            x += 2.5f;
        }
    }

    public static void render(float partialTick) {


        if (heldMaterial != null) {
            RenderSystem.batch.draw(heldMaterial.getItemTexture(), InputSystem.mouse.x - 0.5f, InputSystem.mouse.y - 0.5f, 1, 1);

            if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                boolean droppedOnShip = false;
                for (Ship ship : ShipSystem.getShips()) {
                    if (InputSystem.mouseInBound(ship.getBounds())) {
                        if (!(ship.getWeight()+heldMaterial.getWeight() <= ship.getStorageSpace())) { break; }
                        Supplies supplies = MaterialSystem.findMatchingSupplies(ship.getSuppliesList(),heldMaterial);
                        supplies.add(1);
                        droppedOnShip = true;
                        break;
                    }
                }
                if (!droppedOnShip) {
                    money += heldMaterial.getValue();
                }
                heldMaterial = null;
            } else {
                for (Ship ship : ShipSystem.getShips()) {
                    if (InputSystem.mouseInBound(ship.getBounds())) {
                        if (!(ship.getWeight()+heldMaterial.getWeight() <= ship.getStorageSpace())) {
                            RenderSystem.drawText("[DOESNT FIT]", Mth.map(InputSystem.mouse.x, 0, 30, -600, 600)-70, Mth.map(InputSystem.mouse.y, 0, 26, -310, 740), new Color(1,0.3f,0.3f,1));
                        }
                    }
                }
            }
        }

        if (Maritimes.debug) {
            for (Bounds bounds : buttons) {
                RenderSystem.drawBounds(bounds);
            }
        }
    }

    public static void sellMaterial(Material material, int amount) {
        money += material.getValue()*amount;

    }

    public static void setHeldMaterial(Material heldMaterial) {
        TradingSystem.heldMaterial = heldMaterial;
    }

    public static Material getHeldMaterial() {
        return heldMaterial;
    }

    public static void leftClick() {
        if (heldMaterial == null) {
            for (Button button : buttons) {
                if (InputSystem.mouseInBound(button)) {
                    button.leftClick();
                    break;
                }
            }
        }
    }
}
