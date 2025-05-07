package xyz.volcanobay.maritimes.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import xyz.volcanobay.maritimes.Maritimes;
import xyz.volcanobay.maritimes.core.RenderSystem;
import xyz.volcanobay.maritimes.systems.DateSystem;
import xyz.volcanobay.maritimes.systems.TradingSystem;

public class WorldRenderer {
    private static final Texture WATER = RenderSystem.getTexture("assets/textures/animated/water.png");
    private static final Texture DOCK = RenderSystem.getTexture("assets/textures/world/dock.png");
    private static final Texture DOCK_TOP = RenderSystem.getTexture("assets/textures/world/dock_top.png");

    private static int waterFrame = 0;

    public static void render(float partialTick) {
        if (DateSystem.season.equals(DateSystem.Season.WINTER)) {
            RenderSystem.setColor(0.8f, 0.8f, 0.8f);
        } else {
            RenderSystem.setColor(1f, 1f, 1f);
        }
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        for (int x = 0; x <= 30; x++) {
            for (int y = 0; y <= 30 * (h/w); y++) {
                drawWater(x, y, waterFrame);
            }
        }
        RenderSystem.resetColor();

        RenderSystem.batch.draw(DOCK,0,11.5f,30,4);
        RenderSystem.batch.draw(DOCK_TOP,0,11.5f,30,4);
    }

    public static void tick() {
        if (DateSystem.season == DateSystem.Season.FALL || DateSystem.season == DateSystem.Season.WINTER) {
            waterFrame++;

            if (waterFrame > 59) {
                waterFrame = 0;
            }
        } else {
            waterFrame--;

            if (waterFrame <= 0) {
                waterFrame = 59;
            }
        }
    }

    private static void drawWater(float x, float y, int frame) {
        float difference = 1 / 60f;
        float end = frame / 60f + difference;
        float start = frame / 60f;
        float width = Maritimes.debug ? 0.9f : 1f;
        float offset = Maritimes.debug ? 0.05f : 0;
        RenderSystem.batch.draw(WATER, x + offset, y + offset, width, width, 0, start, 1, end);
    }
}
