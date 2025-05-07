package xyz.volcanobay.maritimes.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import xyz.volcanobay.maritimes.Maritimes;
import xyz.volcanobay.maritimes.render.UIRenderer;
import xyz.volcanobay.maritimes.render.WorldRenderer;
import xyz.volcanobay.maritimes.systems.InputSystem;
import xyz.volcanobay.maritimes.systems.ShipSystem;
import xyz.volcanobay.maritimes.systems.TradingSystem;
import xyz.volcanobay.maritimes.systems.city.City;
import xyz.volcanobay.maritimes.systems.city.CitySystem;
import xyz.volcanobay.maritimes.systems.input.Bounds;
import xyz.volcanobay.maritimes.systems.ship.Ship;
import xyz.volcanobay.maritimes.util.Mth;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class RenderSystem {
    public static OrthographicCamera camera;
    public static SpriteBatch batch;

    public static boolean mapScreen = false;
    public static boolean shipBuilder = false;
    private static float scroll = 0;
    private static float zoom = 5;
    private static Vector2 pos = new Vector2(40, 25);
    private static Vector2 vel = new Vector2();
    private static Vector2 last = new Vector2();

    public static Texture debug = new Texture("assets/textures/screen/debug.png");
    public static Texture map = new Texture("assets/textures/screen/map.png");
    public static Texture marker = new Texture("assets/textures/screen/marker.png");
    public static Texture build = new Texture("assets/textures/screen/build.png");

    public static String viewShipName = "cog";
    private static Ship viewShip = null;

    public static City viewCity;

    public static void register() {
        viewCity = CitySystem.cities.get("mombasa");
        batch = new SpriteBatch();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(30, 30 * (h / w));
        camera.zoom = 1;
        camera.update();
    }

    public static void resetColor() {
        batch.setColor(1, 1, 1, 1);
    }

    public static void setColor(float r, float g, float b) {
        batch.setColor(r, g, b, 1);
    }

    public static void setColor(float r, float g, float b, float a) {
        batch.setColor(r, g, b, a);
    }

    public static void render(float partialTicks) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        if (!mapScreen) {
            camera.position.set(15, 15 * (h / w), 0);
            camera.viewportHeight = 30 * (h / w);
            camera.zoom = Maritimes.debug ? 1.2f : 1;
            camera.update();
            batch.setProjectionMatrix(camera.combined);
            ScreenUtils.clear(0, 0, 0, 1);

            WorldRenderer.render(partialTicks);
            if (!shipBuilder) {
                TradingSystem.renderFirst(partialTicks);
                ShipSystem.renderShips(batch);

                UIRenderer.render(partialTicks);
                batch.draw(build, 28, 1, 2, 2);
                if (Maritimes.debug) {
                    drawOutline(InputSystem.mouse.x - .5f, InputSystem.mouse.x + .5f, InputSystem.mouse.y - .5f, InputSystem.mouse.y + .5f, 0.1f);
                }

                Maritimes.INSTANCE.renderScreen(partialTicks);
                TradingSystem.render(partialTicks);

                if (!ShipSystem.shipsWaiting.isEmpty()) {
                    batch.draw(marker, 28, 6, 2, 2);
                    drawText(String.valueOf(ShipSystem.shipsWaiting.size()), 568, -60, Color.WHITE);
                }
            } else {
                if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                    String picked = "cog";
                    boolean pickNext = false;
                    ArrayList<String> aMessIsWhatThisIs = new ArrayList<>();
                    aMessIsWhatThisIs.addAll(ShipSystem.SHIP_REGISTRY.keySet());
                    aMessIsWhatThisIs.addAll(ShipSystem.SHIP_REGISTRY.keySet());
                    for (String name : aMessIsWhatThisIs) {
                        if (pickNext) {
                            picked = name;
                            viewShip = null;
                            break;
                        }
                        if (name == viewShipName) {
                            pickNext = true;
                        }
                    }
                    viewShipName = picked;
                }
                if (viewShip == null) {
                    viewShip = ShipSystem.SHIP_REGISTRY.get(viewShipName).get();
                }

                viewShip.draw(batch, 15 - (viewShip.getBounds().right - viewShip.getBounds().left), 2, 10, false, true);

                drawText(viewShip.cost() + "£", -500, -210, Color.WHITE);

                UIRenderer.render(partialTicks);
                batch.draw(build, 0, 1, 2, 2);
            }
        } else {

            List<Bounds> cityBounds = new ArrayList<>();

            zoom += scroll / 15f;
            zoom = Math.max(0.4f, Math.min(zoom, 5));
            scroll = scroll / 1.2f;
            camera.position.set(pos.x, pos.y, 0);
            camera.viewportHeight = 30 * (h / w);
            camera.zoom = zoom;
            camera.update();
            batch.setProjectionMatrix(camera.combined);
            ScreenUtils.clear(0, 106 / 255f, 170 / 255f, 1);
            vel.scl(0.8f);
            Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                last = new Vector2(mouse);
            }
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                vel.set(new Vector2(last.x - mouse.x, mouse.y - last.y).scl(0.015f * zoom));
                last = new Vector2(mouse);
            }
            pos.add(vel);



            renderMap(partialTicks);

            for (City city : CitySystem.cities.values()) {
                if (city.hidden)
                    continue;
                cityBounds.add(new Bounds(city.getPosition().x, city.getPosition().x + 1, city.getPosition().y, city.getPosition().y + 1));
                if (InputSystem.mouseInBound(cityBounds.getLast())) {
                    for (City.Connection connection : city.getConnections()) {
                        batch.draw(marker, connection.city.getPosition().x-0.5f, connection.city.getPosition().y-0.5f, 2, 2);
                    }
                }
            }

            if (Maritimes.debug) {
                for (Bounds bounds : cityBounds) {
                    drawBounds(bounds);
                }
            }

            if (Maritimes.debug) {
                drawText("X: " + InputSystem.getMouse().x + ", Y: " + InputSystem.getMouse().y, 0, 0);

            }

            camera.position.set(15, 15 * (h / w), 0);
            camera.viewportHeight = 30 * (h / w);
            camera.zoom = Maritimes.debug ? 1.2f : 1;
            camera.update();
            batch.setProjectionMatrix(camera.combined);

            Vector3 projected = camera.unproject(new Vector3(Gdx.input.getX(),Gdx.input.getY(),0));
            int i = 0;
            for (City city : CitySystem.cities.values()) {
                if (city.hidden)
                    continue;
                Bounds bounds = cityBounds.get(i);
                if (InputSystem.mouseInBound(bounds)) {
                    drawText(city.getName(), Mth.map(projected.x, 0, 30, -600, 600), Mth.map(projected.y, 0, 26, -310, 740), Color.WHITE);
                }
                i++;
            }

            UIRenderer.render(partialTicks);

            camera.position.set(pos.x, pos.y, 0);
            camera.viewportHeight = 30 * (h / w);
            camera.zoom = zoom;
            camera.update();
            batch.setProjectionMatrix(camera.combined);
        }
    }

    public static void scroll(float amount) {
        scroll += amount;
    }


    private static void renderMap(float partialTick) {
        batch.draw(map, 0, 0, 100, 100 * ((float) map.getHeight() / map.getWidth()));


        ShipSystem.renderShips(batch);
        for (City city : CitySystem.cities.values()) {
            if (city.hidden)
                continue;
            batch.draw(marker, city.getPosition().x, city.getPosition().y, 1, 1);
        }

    }

    public static void updateZoom(float newValue) {
        camera.zoom = newValue;
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    public static void drawText(String string, float x, float y) {
        drawText(string, x, y, new Color(65 / 255f, 30 / 255f, 5 / 255f, 255 / 255f));
    }

    public static void drawText(String string, float x, float y, Color color) {
        updateZoom(Maritimes.debug ? 40f * 1.2f : 40f);
        Maritimes.INSTANCE.FONT.setColor(color);
        Maritimes.INSTANCE.FONT.draw(batch, string, x, y);
        Maritimes.INSTANCE.FONT.setColor(1, 1, 1, 1);
        updateZoom(Maritimes.debug ? 1.2f : 1);
    }

    public static void drawBounds(Bounds bounds) {
        if (InputSystem.mouseInBound(bounds)) {
            setColor(1f, 1f, 1f);
        } else {
            setColor(354 / 255f, 60 / 255f, 85 / 255f);
        }
        drawOutline(bounds.left, bounds.right, bounds.bottom, bounds.top, 0.1f);
        setColor(1f, 1f, 1f);
    }

    public static void drawBounds(Bounds bounds, float width) {
        drawOutline(bounds.left, bounds.right, bounds.bottom, bounds.top, width);
    }

    public static void drawOutline(float left, float right, float bottom, float top, float width) {
        batch.draw(RenderSystem.debug, left, bottom, width, top - bottom);
        batch.draw(RenderSystem.debug, right - width, bottom, width, top - bottom);
        batch.draw(RenderSystem.debug, left, bottom, right - left, width);
        batch.draw(RenderSystem.debug, left, top - width, right - left, width);
    }

    public static Texture getTexture(String path) {
        if (Gdx.files.internal(path).exists()) {
            return new Texture(path);
        } else {
            return new Texture("assets/textures/misc/null.png");
        }
    }
}
