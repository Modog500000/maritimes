package xyz.volcanobay.maritimes.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import xyz.volcanobay.maritimes.Maritimes;
import xyz.volcanobay.maritimes.core.RenderSystem;
import xyz.volcanobay.maritimes.systems.DateSystem;
import xyz.volcanobay.maritimes.systems.TradingSystem;
import xyz.volcanobay.maritimes.systems.input.Bounds;

public class UIRenderer {
    private static Texture MONEY = RenderSystem.getTexture("assets/textures/screen/money.png");
    private static Texture DATE = RenderSystem.getTexture("assets/textures/screen/date.png");

    public static void render(float partialTick) {
        RenderSystem.batch.draw(MONEY, 0, 0, 4, 1);
        RenderSystem.drawText(TradingSystem.money + "£", -560, -270,new Color(1,1,1,1));
        RenderSystem.batch.draw(DATE, 24, 0, 6, 1);
        String text = ((int) Math.floor(DateSystem.getTimeUntilSeason()))+ " - " + DateSystem.season.name();
        RenderSystem.drawText(text, 520-(text.length()*10), -270,new Color(1,1,1,1));
    }
}
