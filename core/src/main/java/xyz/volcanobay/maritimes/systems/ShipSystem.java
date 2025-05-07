package xyz.volcanobay.maritimes.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import xyz.volcanobay.maritimes.Maritimes;
import xyz.volcanobay.maritimes.core.RenderSystem;
import xyz.volcanobay.maritimes.systems.city.CitySystem;
import xyz.volcanobay.maritimes.systems.input.Bounds;
import xyz.volcanobay.maritimes.systems.ship.CogShip;
import xyz.volcanobay.maritimes.systems.ship.Ship;
import xyz.volcanobay.maritimes.systems.ship.TreasureShip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class ShipSystem {
    public static final HashMap<String, Supplier<Ship>> SHIP_REGISTRY = new HashMap<>();

    private static List<Ship> ships = new ArrayList<>();
    public static List<Ship> shipsWaiting = new ArrayList<>();

    public static boolean shipPresent = false;

    public static final Supplier<Ship> COG = registerShip(() -> new CogShip("cog"));
    public static final Supplier<Ship> TREASURE_SHIP = registerShip(() -> new TreasureShip("treasure_ship"));

    public static void register() {
        addShip(COG.get());
    }

    public static void tick() {
        if (!shipsWaiting.isEmpty() && !shipPresent) {
            shipsWaiting.getFirst().enter();
            shipsWaiting.removeFirst();
        }
        for (Ship ship : ships) {
            ship.tick();
        }
    }

    public static void addShip(Ship ship) {
        ships.add(ship);
    }

    public static List<Ship> getShips() {
        return ships;
    }

    private static Supplier<Ship> registerShip(Supplier<Ship> supplier) {
        SHIP_REGISTRY.put(supplier.get().getName(), supplier);
        return supplier;
    }

    public static void renderShips(SpriteBatch batch) {
        for (Ship ship : ships) {
            if (RenderSystem.mapScreen) {
                ship.draw(batch, ship.getPosition(Maritimes.partialTicks).x, ship.getPosition(Maritimes.partialTicks).y, 1f,false,false);
            } else {
                if (ship.getEnterTimer() > 0) {
                    ship.draw(batch, (ship.getEnterTimer())/5f, 0, 10f,false,true);
                } else if (ship.getExitTimer() > 0) {
                    ship.draw(batch, (180-ship.getExitTimer())/5f, 5, 10f,true, false);
                } else if (ship.isDockedAtHome()){
                    ship.draw(batch, 0, 2.5f, 10f,false,true);
                }
            }
        }

        if (Maritimes.debug) {
            for (Ship ship : ships) {
                RenderSystem.drawBounds(ship.getBounds());
            }
        }

    }

    public static void input() {
        for (Ship ship : ships) {
            if (InputSystem.mouseInBound(ship.getBounds())) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    ship.leftClick();
                }
                if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
                    ship.middleClick();
                }
                if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    ship.rightClick();
                }
            }
        }

        if (InputSystem.mouseInBound(new Bounds(0,2,1,3))) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                Ship ship = ShipSystem.SHIP_REGISTRY.get(RenderSystem.viewShipName).get();
                if (TradingSystem.money >= ship.cost()) {
                    addShip(ship);
                    TradingSystem.money -= ship.cost();
                }
            }
        }
    }
}
