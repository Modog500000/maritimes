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

public class RenderSystem {
    public static OrthographicCamera camera;
    public static SpriteBatch batch;

    public static boolean mapScreen = false;
    private static float scroll = 0;
    private static float zoom = 5;
    private static Vector2 pos = new Vector2(40, 25);
    private static Vector2 vel = new Vector2();
    private static Vector2 last = new Vector2();

    public static Texture debug = new Texture("assets/textures/screen/debug.png");
    public static Texture map = new Texture("assets/textures/screen/map.png");
    public static Texture marker = new Texture("assets/textures/screen/marker.png");

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
            TradingSystem.renderFirst(partialTicks);
            ShipSystem.renderShips(batch);

            UIRenderer.render(partialTicks);
            if (Maritimes.debug) {
                drawOutline(InputSystem.mouse.x - .5f, InputSystem.mouse.x + .5f, InputSystem.mouse.y - .5f, InputSystem.mouse.y + .5f, 0.1f);
            }
            Maritimes.INSTANCE.renderScreen(partialTicks);
            TradingSystem.render(partialTicks);
        } else {

            zoom += scroll / 10f;
            zoom = Math.max(1, Math.min(zoom, 5));
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

            if (Maritimes.debug) {
                drawText("X: " + InputSystem.getMouse().x + ", Y: " + InputSystem.getMouse().y, 0, 0);
            }

            camera.position.set(15, 15 * (h / w), 0);
            camera.viewportHeight = 30 * (h / w);
            camera.zoom = Maritimes.debug ? 1.2f : 1;
            camera.update();
            batch.setProjectionMatrix(camera.combined);
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

        for (City city : CitySystem.cities.values()) {
            batch.draw(marker, city.getPosition().x, city.getPosition().y, 1, 1);
        }
        ShipSystem.renderShips(batch);

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
        drawOutline(bounds.left, bounds.right, bounds.bottom, bounds.top, 0.1f);
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
