package xyz.volcanobay.maritimes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Timer;
import xyz.volcanobay.maritimes.core.RenderSystem;
import xyz.volcanobay.maritimes.render.WorldRenderer;
import xyz.volcanobay.maritimes.systems.*;
import xyz.volcanobay.maritimes.systems.city.CitySystem;
import xyz.volcanobay.maritimes.systems.input.Clickable;
import xyz.volcanobay.maritimes.util.MartimesInput;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Maritimes extends Game {
    public static Maritimes INSTANCE;
    public static float gameSpeed = 0.05f;
    public static boolean debug = false;
    public BitmapFont FONT = null;

    public static int time = 0;

    private static int frames = 0;
    private static float frameTimes = 0;
    public static float partialTicks = 0f;


    @Override
    public void create() {
        INSTANCE = this;
        MaterialSystem.register(); // Register First
        CitySystem.register();
        RenderSystem.register();
        scheduleTick();
        TradingSystem.money = 1000f;

        ShipSystem.register();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/PixelifySans-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        Gdx.input.setInputProcessor(new MartimesInput());
        FONT = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
    }

    @Override
    public void render() {
        inputs();
        RenderSystem.batch.begin();
        ;
        RenderSystem.render(partialTicks);

        RenderSystem.batch.end();

        frames++;
    }

    public void renderScreen(float partialTicks) {
        if (this.screen != null) {
            this.screen.render(partialTicks);
        }
    }

    public static void inputs() {
        InputSystem.input();
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            RenderSystem.mapScreen = !RenderSystem.mapScreen;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (RenderSystem.mapScreen) {
                RenderSystem.mapScreen = false;
            } else {
                INSTANCE.setScreen(null);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debug = !debug;
        }
        if (INSTANCE.screen instanceof Clickable clickable && InputSystem.mouseInBound(clickable.getBounds())) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                clickable.leftClick();
            }
            if (Gdx.input.isButtonJustPressed(Input.Buttons.MIDDLE)) {
                clickable.middleClick();
            }
            if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                clickable.rightClick();
            }
        } else {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                TradingSystem.leftClick();
            }
            ShipSystem.input();
        }
    }

    public static void tick() {
        WorldRenderer.tick();
        DateSystem.tick();
        ShipSystem.tick();
        time++;
        frameTimes = 20f / frames;
        frames = 0;
        partialTicks = Math.clamp(frameTimes * frames, 0, 1);
    }

    public static void scheduleTick() {
        tick();
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                scheduleTick();
            }
        }, gameSpeed);
    }
}
